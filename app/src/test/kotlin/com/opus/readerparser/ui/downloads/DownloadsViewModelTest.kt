package com.opus.readerparser.ui.downloads

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.opus.readerparser.domain.model.Chapter
import com.opus.readerparser.domain.model.ChapterWithState
import com.opus.readerparser.domain.model.ContentType
import com.opus.readerparser.domain.model.DownloadItem
import com.opus.readerparser.domain.model.DownloadState
import com.opus.readerparser.domain.model.DownloadStorageBreakdown
import com.opus.readerparser.domain.model.Series
import com.opus.readerparser.fakes.FakeChapterRepository
import com.opus.readerparser.fakes.FakeDownloadRepository
import com.opus.readerparser.fakes.FakeSeriesRepository
import com.opus.readerparser.testutil.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DownloadsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repo: FakeDownloadRepository
    private lateinit var seriesRepo: FakeSeriesRepository
    private lateinit var chapterRepo: FakeChapterRepository
    private lateinit var vm: DownloadsViewModel

    private fun item(chapterUrl: String, state: DownloadState = DownloadState.QUEUED) = DownloadItem(
        sourceId = 1L,
        chapterUrl = chapterUrl,
        state = state,
        progress = 0f,
        errorMessage = null,
    )

    @Before
    fun setUp() {
        repo = FakeDownloadRepository()
        seriesRepo = FakeSeriesRepository()
        chapterRepo = FakeChapterRepository()
        vm = DownloadsViewModel(repo, seriesRepo, chapterRepo)
    }

    @Test
    fun `init emits empty download list`() = runTest {
        assertThat(vm.state.value.downloads).isEmpty()
    }

    @Test
    fun `queue updates reflected in state`() = runTest {
        vm.state.test {
            awaitItem() // empty

            repo.setQueue(listOf(item("https://test.invalid/ch/1")))
            assertThat(awaitItem().downloads).hasSize(1)
        }
    }

    @Test
    fun `Cancel removes item from state and calls repository`() = runTest {
        val chapterUrl = "https://test.invalid/ch/1"
        vm.state.test {
            awaitItem() // empty

            repo.setQueue(listOf(item(chapterUrl)))
            awaitItem() // one item

            vm.onAction(DownloadsAction.Cancel(1L, chapterUrl))
            assertThat(awaitItem().downloads).isEmpty()
        }
        assertThat(repo.cancelCalls).containsExactly(1L to chapterUrl)
    }

    @Test
    fun `Retry resets failed item state and calls repository`() = runTest {
        val chapterUrl = "https://test.invalid/ch/1"
        vm.state.test {
            awaitItem() // empty

            repo.setQueue(listOf(item(chapterUrl, DownloadState.FAILED)))
            awaitItem() // failed item

            vm.onAction(DownloadsAction.Retry(1L, chapterUrl))
            val updated = awaitItem()
            assertThat(updated.downloads.first().state).isEqualTo(DownloadState.QUEUED)
        }
        assertThat(repo.retryCalls).containsExactly(1L to chapterUrl)
    }

    @Test
    fun `Delete removes item from state and calls repository`() = runTest {
        val chapterUrl = "https://test.invalid/ch/1"
        vm.state.test {
            awaitItem() // empty

            repo.setQueue(listOf(item(chapterUrl, DownloadState.COMPLETED)))
            awaitItem() // completed item

            vm.onAction(DownloadsAction.Delete(1L, chapterUrl))
            assertThat(awaitItem().downloads).isEmpty()
        }
        assertThat(repo.deleteDownloadCalls).containsExactly(1L to chapterUrl)
    }

    @Test
    fun `storage breakdown bytes reflected in state`() = runTest {
        val expected = DownloadStorageBreakdown(
            novelTextBytes = 12_800_000L,
            manhwaImageBytes = 30_000_000L,
            availableBytes = 64_000_000_000L,
        )
        repo.storageBreakdown = expected

        vm.state.test {
            awaitItem()

            repo.setQueue(listOf(item("https://test.invalid/ch/1")))
            val state = awaitItem()
            assertThat(state.novelTextBytes).isEqualTo(12_800_000L)
            assertThat(state.manhwaImageBytes).isEqualTo(30_000_000L)
            assertThat(state.availableBytes).isEqualTo(64_000_000_000L)
            assertThat(state.storageBreakdown).isEqualTo(expected)
        }
    }

    @Test
    fun `grouping downloaded chapters by Series into DownloadedSeriesItem with range summary and contentType`() = runTest {
        val series = Series(
            sourceId = 1L,
            url = "https://test.invalid/series/solo",
            title = "Solo Leveling",
            type = ContentType.MANHWA,
        )
        val ch1 = Chapter(
            seriesUrl = series.url,
            sourceId = 1L,
            url = "https://test.invalid/ch/1",
            name = "Chapter 1",
            number = 1f,
        )
        val ch2 = Chapter(
            seriesUrl = series.url,
            sourceId = 1L,
            url = "https://test.invalid/ch/2",
            name = "Chapter 2",
            number = 2f,
        )

        chapterRepo.setChapters(
            series.url,
            listOf(
                ChapterWithState(ch1, read = false, downloaded = true, progress = 0f),
                ChapterWithState(ch2, read = false, downloaded = true, progress = 0f),
            ),
        )

        vm.state.test {
            awaitItem() // initial

            seriesRepo.addToLibrary(series)

            val updated = awaitItem()
            assertThat(updated.downloadedSeries).hasSize(1)
            val seriesItem = updated.downloadedSeries.first()
            assertThat(seriesItem.seriesTitle).isEqualTo("Solo Leveling")
            assertThat(seriesItem.contentType).isEqualTo(ContentType.MANHWA)
            assertThat(seriesItem.downloadedChapterCount).isEqualTo(2)
            assertThat(seriesItem.chapterRangeSummary).isEqualTo("2 chapters · Ch. 1–2")
        }
    }

    @Test
    fun `ClearSeriesDownloads action deletes downloads, marks chapters not downloaded, and removes series`() = runTest {
        val series = Series(
            sourceId = 1L,
            url = "https://test.invalid/series/solo",
            title = "Solo Leveling",
            type = ContentType.MANHWA,
        )
        val ch1 = Chapter(
            seriesUrl = series.url,
            sourceId = 1L,
            url = "https://test.invalid/ch/1",
            name = "Chapter 1",
            number = 1f,
        )
        val ch2 = Chapter(
            seriesUrl = series.url,
            sourceId = 1L,
            url = "https://test.invalid/ch/2",
            name = "Chapter 2",
            number = 2f,
        )

        chapterRepo.setChapters(
            series.url,
            listOf(
                ChapterWithState(ch1, read = false, downloaded = true, progress = 0f),
                ChapterWithState(ch2, read = false, downloaded = true, progress = 0f),
            ),
        )
        seriesRepo.addToLibrary(series)

        repo.storageBreakdown = DownloadStorageBreakdown(
            novelTextBytes = 0L,
            manhwaImageBytes = 50_000_000L,
            availableBytes = 64_000_000_000L,
        )

        vm.state.test {
            val populated = awaitItem()
            assertThat(populated.downloadedSeries).hasSize(1)

            repo.storageBreakdown = DownloadStorageBreakdown(
                novelTextBytes = 0L,
                manhwaImageBytes = 0L,
                availableBytes = 64_000_000_000L,
            )

            vm.onAction(DownloadsAction.ClearSeriesDownloads(series.url))

            val cleared = awaitItem()
            assertThat(cleared.downloadedSeries).isEmpty()
            assertThat(cleared.manhwaImageBytes).isEqualTo(0L)
        }

        assertThat(repo.deleteDownloadCalls).contains(1L to ch1.url)
        assertThat(repo.deleteDownloadCalls).contains(1L to ch2.url)
        assertThat(chapterRepo.markDownloadedCalls).contains(ch1 to false)
        assertThat(chapterRepo.markDownloadedCalls).contains(ch2 to false)
    }
}
