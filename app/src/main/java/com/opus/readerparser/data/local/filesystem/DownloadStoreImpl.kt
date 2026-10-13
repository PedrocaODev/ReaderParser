package com.opus.readerparser.data.local.filesystem

import com.opus.readerparser.core.di.DownloadRoot
import com.opus.readerparser.core.util.hashUrl
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

/**
 * Real file-system implementation of [DownloadStore].
 *
 * The root directory is injected so JVM tests can point it at a temp folder
 * without needing an Android context. In production Hilt provides
 * `File(context.filesDir, "downloads")`.
 *
 * Directory layout relative to [root]:
 * ```
 * {sourceId}/
 *   {hashUrl(chapter.seriesUrl)}/
 *     {hashUrl(chapter.url)}/
 *       meta.json
 *       content.html   (NOVEL)
 *       001.jpg        (MANHWA, 1-based, zero-padded 3 digits)
 *       002.jpg
 *       ...
 * ```
 */
class DownloadStoreImpl @Inject constructor(
    @param:DownloadRoot private val root: File,
    private val json: Json,
) : DownloadStore {

    @Serializable
    private data class Meta(
        val type: String,
        @SerialName("originalUrls") val originalUrls: List<String> = emptyList(),
        val pageCount: Int = 0,
        val downloadedAt: Long,
    )

    // ---- path helpers -------------------------------------------------------

    private fun chapterDir(chapter: Chapter): File =
        root.resolve("${chapter.sourceId}")
            .resolve(hashUrl(chapter.seriesUrl))
            .resolve(hashUrl(chapter.url))

    private fun metaFile(dir: File): File = dir.resolve("meta.json")
    private fun contentFile(dir: File): File = dir.resolve("content.html")
    private fun pageFile(dir: File, index: Int): File =
        dir.resolve("%03d.jpg".format(index))

    private fun commitStaging(stagingDir: File, targetDir: File) {
        val parent = targetDir.parentFile ?: throw IOException("Invalid chapter directory parent for $targetDir")
        if (!parent.exists()) {
            parent.mkdirs()
        }

        var backupDir: File? = null
        if (targetDir.exists()) {
            val backup = File(parent, ".backup-${targetDir.name}-${UUID.randomUUID()}")
            if (!targetDir.renameTo(backup)) {
                throw IOException("Failed to rename target directory $targetDir to backup $backup")
            }
            backupDir = backup
        }

        val renamed = stagingDir.renameTo(targetDir)
        if (!renamed) {
            if (backupDir != null && backupDir.exists()) {
                backupDir.renameTo(targetDir)
            }
            throw IOException("Failed to rename staging directory $stagingDir to target $targetDir")
        }

        backupDir?.deleteRecursively()
    }

    // ---- DownloadStore ------------------------------------------------------

    override suspend fun read(chapter: Chapter): ChapterContent? =
        withContext(Dispatchers.IO) {
            val dir = chapterDir(chapter)
            val metaF = metaFile(dir)
            if (!metaF.exists() || !metaF.canRead() || metaF.length() == 0L) return@withContext null

            val meta = try {
                json.decodeFromString<Meta>(metaF.readText())
            } catch (_: Exception) {
                return@withContext null
            }

            when (meta.type) {
                "NOVEL" -> {
                    val contentF = contentFile(dir)
                    if (!contentF.exists() || !contentF.canRead() || contentF.length() == 0L) {
                        return@withContext null
                    }
                    val html = try {
                        contentF.readText()
                    } catch (_: Exception) {
                        return@withContext null
                    }
                    ChapterContent.Text(html)
                }
                "MANHWA" -> {
                    if (meta.pageCount <= 0) return@withContext null
                    val pageFiles = (1..meta.pageCount).map { pageFile(dir, it) }
                    for (page in pageFiles) {
                        if (!page.exists() || !page.canRead() || page.length() == 0L) {
                            return@withContext null
                        }
                    }
                    val pages = pageFiles.map { it.toURI().toString() }
                    ChapterContent.Pages(pages)
                }
                else -> null
            }
        }

    override suspend fun writeNovel(chapter: Chapter, html: String) =
        withContext(Dispatchers.IO) {
            val dir = chapterDir(chapter)
            val parent = dir.parentFile ?: throw IOException("Invalid chapter directory parent for $dir")
            if (!parent.exists()) {
                parent.mkdirs()
            }
            val stagingDir = File(parent, ".staging-${dir.name}-${UUID.randomUUID()}")
            val meta = Meta(
                type = "NOVEL",
                pageCount = 0,
                downloadedAt = System.currentTimeMillis(),
            )
            try {
                if (!stagingDir.mkdirs()) {
                    throw IOException("Failed to create staging directory: $stagingDir")
                }
                contentFile(stagingDir).writeText(html)
                metaFile(stagingDir).writeText(json.encodeToString(meta))
                commitStaging(stagingDir, dir)
            } finally {
                if (stagingDir.exists()) {
                    stagingDir.deleteRecursively()
                }
            }
        }

    override suspend fun writeManhwa(
        chapter: Chapter,
        imageUrls: List<String>,
        fetchBytes: suspend (url: String) -> ByteArray,
        onPageDownloaded: suspend (pagesDownloaded: Int, totalPages: Int) -> Unit,
    ) {
        val dir = chapterDir(chapter)
        val parent = dir.parentFile ?: throw IOException("Invalid chapter directory parent for $dir")
        withContext(Dispatchers.IO) {
            if (!parent.exists()) {
                parent.mkdirs()
            }
        }
        val stagingDir = File(parent, ".staging-${dir.name}-${UUID.randomUUID()}")
        try {
            withContext(Dispatchers.IO) {
                if (!stagingDir.mkdirs()) {
                    throw IOException("Failed to create staging directory: $stagingDir")
                }
            }

            imageUrls.forEachIndexed { index, url ->
                val bytes = fetchBytes(url)          // network — caller's dispatcher
                withContext(Dispatchers.IO) {         // disk — IO dispatcher
                    pageFile(stagingDir, index + 1).writeBytes(bytes)
                }
                onPageDownloaded(index + 1, imageUrls.size)
            }

            withContext(Dispatchers.IO) {
                for (i in 1..imageUrls.size) {
                    val page = pageFile(stagingDir, i)
                    if (!page.exists() || !page.canRead() || page.length() == 0L) {
                        throw IOException("Page $i is missing, unreadable, or empty in $stagingDir")
                    }
                }

                val meta = Meta(
                    type = "MANHWA",
                    originalUrls = imageUrls,
                    pageCount = imageUrls.size,
                    downloadedAt = System.currentTimeMillis(),
                )
                metaFile(stagingDir).writeText(json.encodeToString(meta))
                commitStaging(stagingDir, dir)
            }
        } finally {
            if (stagingDir.exists()) {
                stagingDir.deleteRecursively()
            }
        }
    }

    override suspend fun delete(chapter: Chapter): Unit =
        withContext(Dispatchers.IO) {
            chapterDir(chapter).deleteRecursively()
        }

    override suspend fun deleteByHash(sourceId: Long, chapterUrlHash: String): Boolean =
        withContext(Dispatchers.IO) {
            val sourceDir = root.resolve("$sourceId")
            if (!sourceDir.exists()) return@withContext false
            // Search all series subdirectories for a matching chapter hash
            val seriesDirs = sourceDir.listFiles() ?: return@withContext false
            for (seriesDir in seriesDirs) {
                if (!seriesDir.isDirectory) continue
                val chapterDir = seriesDir.resolve(chapterUrlHash)
                if (chapterDir.exists()) {
                    chapterDir.deleteRecursively()
                    return@withContext true
                }
            }
            false
        }
}
