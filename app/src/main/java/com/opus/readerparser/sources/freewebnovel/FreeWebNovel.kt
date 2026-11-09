package com.opus.readerparser.sources.freewebnovel

import com.opus.readerparser.core.util.computeSourceId
import com.opus.readerparser.data.local.database.dao.SeriesDao
import com.opus.readerparser.data.source.CatalogHarvester
import com.opus.readerparser.data.source.HtmlSource
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterContent
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.FilterList
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.domain.model.SeriesPage
import com.opus.readerparser.domain.model.SeriesStatus
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

private const val USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36"
private const val ACCEPT_HEADER =
    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
private const val ACCEPT_LANGUAGE = "en-US,en;q=0.9"

/**
 * Source plugin for [Free Web Novel](https://freewebnovel.com/), an English webnovel aggregator.
 *
 * The site serves server-rendered HTML with a consistent card-based listing layout.
 * Parser selectors extract data from the static HTML served on initial load.
 *
 * ### URL patterns
 * - Popular listing : `/sort/most-popular` (single page, no pagination)
 * - Latest listing  : `/sort/latest-release` (page 1), `/sort/latest-release/{page}` (page 2+)
 * - Series detail   : `/novel/{slug}`
 * - Chapter         : `/novel/{slug}/chapter-{n}`
 * - Search          : `/search?keyword={query}`
 */
class FreeWebNovel(
    client: HttpClient,
    private val seriesDao: SeriesDao? = null,
    private val crawlDelayMs: Long = 300L,
    private val paginationDelayMs: Long = 100L,
) : HtmlSource(client), CatalogHarvester {

    private val batchPersister = seriesDao?.let { FreeWebNovelCatalogBatchPersister(it) }

    // =========================================================================
    // Source identity
    // =========================================================================

    override val id: Long = computeSourceId("FreeWebNovel", "en", ContentType.NOVEL)
    override val name: String = "FreeWebNovel"
    override val lang: String = "en"
    override val baseUrl: String = "https://freewebnovel.com"
    override val type: ContentType = ContentType.NOVEL

    override suspend fun fetchDocBody(url: String): String =
        client.get(url) {
            header("User-Agent", USER_AGENT)
            header("Accept", ACCEPT_HEADER)
            header("Accept-Language", ACCEPT_LANGUAGE)
        }.bodyAsText()

    // =========================================================================
    // getPopular — most-popular listing
    // =========================================================================

    /**
     * The popular listing is a single non-paginated page.
     */
    override fun popularUrl(page: Int): String = "$baseUrl/sort/most-popular"

    /**
     * Matches each series card on the popular/latest/search listing pages.
     *
     * HTML structure:
     * ```
     * <div class="li-row">
     *   <div class="li">
     *     <div class="con">
     *       <div class="pic">
     *         <a href="/novel/{slug}"><img src="{coverUrl}" /></a>
     *       </div>
     *       <div class="txt">
     *         <h3 class="tit"><a href="/novel/{slug}" title="{title}">{title}</a></h3>
     *         <div class="desc">
     *           <div class="item">...</div>
     *           <div class="item">
     *             <a href="/genre/{genre}" class="novel">{genre}</a>
     *           </div>
     *           <div class="item">
     *             <a class="chapter" ...><span class="s2">Full</span>...</a>
     *           </div>
     *         </div>
     *       </div>
     *     </div>
     *   </div>
     * </div>
     * ```
     */
    override fun popularSelector(): String = "div.ul-list1.ul-list1-2.ss-custom > div.li-row"

    /**
     * Extracts a [Series] from a listing card.
     *
     * Completion is inferred from a `<span class="s2">Full</span>` marker
     * inside the chapter link.  Series without this marker are left as
     * [SeriesStatus.UNKNOWN] — the series details page provides the
     * definitive status.
     */
    override fun seriesFromElement(el: Element): Series {
        val titleLink = el.selectFirst("h3.tit a[href^=\"/novel/\"]")
            ?: error("Series card missing title link")
        val url = titleLink.absUrl("href")
        val title = titleLink.text().trim()
        val coverImg = el.selectFirst(".pic a img")
        val coverUrl = coverImg?.absUrl("src")
        val status = if (el.selectFirst("span.s2")?.text()?.trim() == "Full") {
            SeriesStatus.COMPLETED
        } else {
            SeriesStatus.UNKNOWN
        }
        return Series(
            sourceId = id,
            url = url,
            title = title,
            coverUrl = coverUrl,
            status = status,
            type = type,
        )
    }

    /** Popular page has no pagination — returns `null`. */
    override fun popularNextPageSelector(): String? = null

    // =========================================================================
    // getLatest — latest-release listing (paginated)
    // =========================================================================

    /**
     * Fetches the latest-release listing at `/sort/latest-release[/{page}]`.
     *
     * The page uses the same card markup as the popular listing.  Pagination is
     * detected by the presence of a `>>` link inside `.pages`.
     *
     * ```
     * <div class="pages">
     *   <ul><li>
     *     <a href="/sort/latest-release/1">1</a>
     *     <strong>1</strong>
     *     <a href="/sort/latest-release/2">2</a>
     *     ...
     *     <a href="/sort/latest-release/2">&gt;&gt;</a>
     *   </li></ul>
     * </div>
     * ```
     */
    override suspend fun getLatest(page: Int): SeriesPage {
        val url = if (page == 1) "$baseUrl/sort/latest-release" else "$baseUrl/sort/latest-release/$page"
        val doc = fetchDoc(url)
        val series = doc.select(popularSelector()).map(::seriesFromElement)
        // Exclude javascript:void(0) links — the site renders >> on the last page
        // but with href="javascript:void(0);" instead of a real page URL.
        val hasNext = doc.selectFirst("div.pages a:matches(>>):not([href*=\"javascript\"])") != null
        return SeriesPage(series, hasNext)
    }

    // =========================================================================
    // search
    // =========================================================================

    /**
     * Search uses the same card structure as the popular listing.
     * The site search form submits via POST but also responds to GET
     * at `/search?keyword={query}`.
     */
    override fun searchUrl(query: String, page: Int, filters: FilterList): String {
        val encoded = java.net.URLEncoder.encode(query, "UTF-8")
        return "$baseUrl/search?keyword=$encoded"
    }

    override suspend fun search(query: String, page: Int, filters: FilterList): SeriesPage {
        val trimmed = query.trim()
        if (trimmed.startsWith("$baseUrl/novel/") || trimmed.startsWith("/novel/") || (trimmed.startsWith("http") && trimmed.contains("/novel/"))) {
            val fullUrl = if (trimmed.startsWith("http")) trimmed else "$baseUrl$trimmed"
            val dummy = Series(
                sourceId = id,
                url = fullUrl,
                title = "",
                type = type,
            )
            return try {
                val enriched = getSeriesDetails(dummy)
                SeriesPage(listOf(enriched), hasNextPage = false)
            } catch (_: Exception) {
                super.search(query, page, filters)
            }
        }
        return super.search(query, page, filters)
    }

    // =========================================================================
    // getSeriesDetails
    // =========================================================================

    /**
     * Enriches [series] with details from the series detail page.
     *
     * Key selectors on the detail page:
     * - **Title**       : `.m-desc h1.tit`
     * - **Cover**       : `.m-book1 .pic img`
     * - **Author**      : `a[href^="/author/"]`
     * - **Genres**      : `.m-book1 .txt .item a[href^="/genre/"]`
     * - **Status**      : `.item:has(.glyphicon-time) .right .s1 a` (matches `.s1.s2` for OnGoing, `.s1.s3` for Completed)
     * - **Description** : `.m-desc .txt .inner`
     */
    override fun seriesDetailsParse(doc: Document, series: Series): Series {
        val author = doc.selectFirst("a[href^=\"/author/\"]")?.text()?.trim()
        val descriptionEl = doc.selectFirst(".m-desc .txt .inner")
        val description = cleanDescription(descriptionEl)
        val genres = doc.select(".m-book1 .txt .item a[href^=\"/genre/\"]")
            .map { it.text().trim() }
        // Use .s1 alone so it matches both .s1.s2 (OnGoing) and .s1.s3 (Completed).
        val statusEl = doc.selectFirst(
            ".m-book1 .txt .item:has(.glyphicon-time) .right .s1 a",
        )
        val status = parseDetailStatus(statusEl?.text()?.trim())
        val coverUrl = doc.selectFirst(".m-book1 .pic img")?.absUrl("src")
        val extractedTitle = doc.selectFirst("h1.tit")?.text()?.trim().orEmpty()
        val title = if (extractedTitle.isNotBlank()) extractedTitle
                    else series.title.takeIf { it.isNotBlank() } ?: ""

        return series.copy(
            title = title,
            author = author,
            description = description,
            genres = genres,
            status = status,
            coverUrl = coverUrl ?: series.coverUrl,
        )
    }

    /**
     * Cleans an HTML description element into plain text.
     *
     * - Strips all raw HTML tags (<p>, </p>, <div>, <br>, <span>, etc.)
     * - Unescapes HTML entities
     * - Preserves paragraph formatting as double newlines (`\n\n`)
     */
    private fun cleanDescription(element: Element?): String? {
        if (element == null) return null
        val pElements = element.select("p")
        val rawText = if (pElements.isNotEmpty()) {
            pElements.map { p ->
                cleanElementText(p)
            }.filter { it.isNotBlank() }
            .joinToString("\n\n")
        } else {
            val divElements = element.select("div")
            if (divElements.isNotEmpty()) {
                divElements.map { div ->
                    cleanElementText(div)
                }.filter { it.isNotBlank() }
                .joinToString("\n\n")
            } else {
                cleanElementText(element)
            }
        }
        return rawText.trim().takeIf { it.isNotBlank() }
    }

    private fun cleanElementText(element: Element): String {
        val clone = element.clone()
        clone.select("br").forEach { it.replaceWith(org.jsoup.nodes.TextNode("\n")) }
        return clone.wholeText().lines().map { it.trim() }.filter { it.isNotEmpty() }.joinToString("\n")
    }

    // =========================================================================
    // getChapterList
    // =========================================================================

    /**
     * Matches each chapter row in the full chapter list (`ul#idData`).
     *
     * HTML structure:
     * ```
     * <ul class="ul-list5" id="idData">
     *   <li>
     *     <span class="glyphicon glyphicon-book right-5"></span>
     *     <a href="/novel/{slug}/chapter-{n}" class="con"
     *        title="Chapter {n}: {title}">Chapter {n}: {title}</a>
     *   </li>
     *   ...
     * </ul>
     * ```
     *
     * The list is server-rendered, ascending from chapter 1 upward.
     * Per-row dates are not available, so [Chapter.uploadDate] is `null`.
     */
    override fun chapterListSelector(): String = "ul#idData a.con[href*=\"/chapter-\"]"

    @Serializable
    private data class ChapterListAjaxResponse(
        val code: Int = 0,
        val html: String = "",
        val page: Int = 0,
        val pageSize: Int = 0,
        val totalPage: Int = 0,
        val totalChapters: Int = 0,
    )

    companion object {
        private val json = Json { ignoreUnknownKeys = true }
        private const val chapterListAjaxPageSize = 200
        private val CHAPTER_NUMBER_REGEX = Regex("""Chapter\s+(\d+(?:\.\d+)?)""", RegexOption.IGNORE_CASE)
    }

    override fun fetchChapterListProgressive(series: Series): Flow<List<Chapter>> = flow {
        val firstPage = fetchOptionalChapterListAjax(series.url, 1)
        if (firstPage == null || firstPage.html.isBlank()) {
            val doc = fetchDoc(series.url)
            emit(doc.select(chapterListSelector()).map { chapterFromElement(it, series) })
            return@flow
        }

        val page1Chapters = parseChaptersFragment(firstPage.html, series)
        if (page1Chapters.isEmpty()) {
            throw IllegalStateException("Expected chapters on page 1 for ${series.url}, found 0")
        }

        val chaptersByUrl = LinkedHashMap<String, Chapter>()
        for (chapter in page1Chapters) {
            chaptersByUrl.putIfAbsent(chapter.url, chapter)
        }
        emit(chaptersByUrl.values.toList())

        val totalPage = firstPage.totalPage.coerceAtLeast(1)
        if (totalPage > 1) {
            for (page in 2..totalPage) {
                if (paginationDelayMs > 0) {
                    delay(paginationDelayMs)
                }
                val payload = fetchChapterListAjaxWithRetry(series.url, page)
                if (payload == null || payload.html.isBlank()) {
                    logWarning("Continuation page $page of $totalPage failed or had blank HTML for ${series.url}; returning partial chapters gathered so far")
                    break
                }
                val pageChapters = parseChaptersFragment(payload.html, series)
                if (pageChapters.isEmpty()) {
                    logWarning("Continuation page $page of $totalPage contained no chapters for ${series.url}; returning partial chapters gathered so far")
                    break
                }
                var addedOnPage = 0
                for (chapter in pageChapters) {
                    if (chaptersByUrl.putIfAbsent(chapter.url, chapter) == null) {
                        addedOnPage++
                    }
                }
                if (addedOnPage == 0) {
                    logWarning("Continuation page $page of $totalPage contained no new chapters for ${series.url}; returning partial chapters gathered so far")
                    break
                }
                emit(chaptersByUrl.values.toList())
            }
        }
    }

    override suspend fun getChapterList(series: Series): List<Chapter> =
        fetchChapterListProgressive(series).last()

    private fun parseChaptersFragment(html: String, series: Series): List<Chapter> {
        val ajaxDoc = Jsoup.parse(html, series.url)
        // ponytail: AJAX html fragment has bare <li> rows, no wrapping <ul id="idData">
        return ajaxDoc.select("a.con[href*=\"/chapter-\"]")
            .map { chapterFromElement(it, series) }
    }

    private suspend fun fetchChapterListAjaxWithRetry(
        seriesUrl: String,
        page: Int,
        maxRetries: Int = 2,
        initialBackoffMs: Long = 100L,
    ): ChapterListAjaxResponse? {
        var attempt = 0
        var currentBackoff = initialBackoffMs
        while (true) {
            attempt++
            try {
                val ajaxUrl = "$seriesUrl?ajax=chapters&page=$page&pageSize=$chapterListAjaxPageSize"
                val response = client.get(ajaxUrl) {
                    header("X-Requested-With", "XMLHttpRequest")
                }
                val statusCode = response.status.value
                if (statusCode in 200..299) {
                    val body = response.bodyAsText()
                    return decodeChapterListAjaxResponse(body)
                }
                if (isTransientHttpError(statusCode) && attempt <= maxRetries) {
                    delay(currentBackoff)
                    currentBackoff *= 2
                    continue
                }
                return null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (isTransientException(e) && attempt <= maxRetries) {
                    delay(currentBackoff)
                    currentBackoff *= 2
                    continue
                }
                return null
            }
        }
    }

    private fun isTransientHttpError(statusCode: Int): Boolean =
        statusCode == 429 || statusCode == 503 || statusCode == 504 || statusCode == 408

    private fun isTransientException(e: Exception): Boolean =
        e is java.io.IOException || e is java.net.SocketException ||
            e.javaClass.simpleName.contains("Timeout", ignoreCase = true)

    private suspend fun fetchOptionalChapterListAjax(
        seriesUrl: String,
        page: Int,
    ): ChapterListAjaxResponse? = fetchChapterListAjaxWithRetry(seriesUrl, page)

    private fun logWarning(message: String, throwable: Throwable? = null) {
        try {
            android.util.Log.w("FreeWebNovel", message, throwable)
        } catch (_: Throwable) {
            System.err.println("FreeWebNovel: $message")
            throwable?.printStackTrace(System.err)
        }
    }

    private fun decodeChapterListAjaxResponse(body: String): ChapterListAjaxResponse? = try {
        json.decodeFromString<ChapterListAjaxResponse>(body)
    } catch (_: SerializationException) {
        null
    }

    /** Extracts a [Chapter] from a chapter-list row. */
    override fun chapterFromElement(el: Element, series: Series): Chapter {
        val url = el.absUrl("href").takeIf { it.isNotBlank() }
            ?: error("Chapter row missing valid URL")
        val name = el.text().trim()
        val number = parseChapterNumber(name)
        return Chapter(
            seriesUrl = series.url,
            sourceId = id,
            url = url,
            name = name,
            number = number,
            uploadDate = null,
        )
    }

    // =========================================================================
    // getChapterContent — novel text
    // =========================================================================

    /**
     * Extracts the cleaned chapter HTML from the chapter page.
     *
     * The chapter body lives inside `<div id="article">`, which contains
     * an `<h4>` title followed by `<p>` paragraph elements.  Inline ad
     * containers ([id^="pf-"], [id^="bg-ssp-]), `<script>` blocks, and
     * `<subtxt>` watermark nodes are removed before returning the HTML.
     */
    override fun chapterTextParse(doc: Document): String {
        val article = doc.selectFirst("div#article")
            ?: error("Chapter page missing div#article container")
        article.select("div[id^=\"pf-\"]").remove()
        article.select("div[id^=\"bg-ssp-\"]").remove()
        article.select("script").remove()
        article.select("subtxt").remove()
        return article.html().trim()
    }

    override fun chapterPagesParse(doc: Document): List<String> =
        error("Override for manhwa sources — FreeWebNovel is a novel source")

    // =========================================================================
    // CatalogHarvester — exhaustive catalog crawling & batch persistence
    // =========================================================================

    /**
     * Sequentially crawls the `/sort/latest-novel/{page}` directory, extracting
     * series cards into [Series] stubs and emitting page batches.
     *
     * If [seriesDao] was provided at construction, each batch is automatically
     * persisted to Room with `inLibrary = false` and zero chapters fetched or inserted.
     * Crawling terminates when next-page pagination (`>>`) is absent or points to javascript:void,
     * or when an empty series page is encountered.
     */
    override suspend fun harvestCatalog(): Flow<List<Series>> = flow {
        var page = 1
        while (true) {
            val url = directoryUrl(page)
            val doc = fetchDoc(url)
            val series = doc.select(popularSelector()).map(::seriesFromElement)
            if (series.isNotEmpty()) {
                batchPersister?.persistBatch(series)
                emit(series)
            }
            val hasNext = doc.selectFirst("div.pages a:matches(>>):not([href*=\"javascript\"])") != null
            if (!hasNext || series.isEmpty()) {
                break
            }
            page++
            if (crawlDelayMs > 0) {
                delay(crawlDelayMs)
            }
        }
    }

    fun directoryUrl(page: Int): String = "$baseUrl/sort/latest-novel/$page"

    suspend fun persistBatch(seriesList: List<Series>): Int? = batchPersister?.persistBatch(seriesList)

    suspend fun persistBatch(dao: SeriesDao, seriesList: List<Series>): Int =
        FreeWebNovelCatalogBatchPersister(dao).persistBatch(seriesList)

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Parses the status text from the series detail page.
     * The site uses "OnGoing" and "Completed".
     */
    private fun parseDetailStatus(text: String?): SeriesStatus = when {
        text.isNullOrBlank() -> SeriesStatus.UNKNOWN
        text.contains("completed", ignoreCase = true) -> SeriesStatus.COMPLETED
        text.contains("ongoing", ignoreCase = true) -> SeriesStatus.ONGOING
        else -> SeriesStatus.UNKNOWN
    }

    /**
     * Extracts the chapter number from a chapter name string.
     *
     * Supported formats:
     * - `"Chapter 876: Restless [3]"` → 876f
     * - `"Chapter 1: Prologue [1]"` → 1f
     * - `"Chapter 2233.2: Getting Married..."` → 2233.2f
     */
    private fun parseChapterNumber(name: String): Float {
        return CHAPTER_NUMBER_REGEX.find(name)?.groupValues?.get(1)?.toFloatOrNull() ?: -1f
    }
}
