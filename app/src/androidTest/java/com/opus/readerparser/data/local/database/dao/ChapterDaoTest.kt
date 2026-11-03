package com.opus.readerparser.data.local.database.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.opus.readerparser.data.local.database.AppDatabase
import com.opus.readerparser.data.local.database.entities.ChapterEntity
import com.opus.readerparser.data.local.database.entities.SeriesEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChapterDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: ChapterDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.chapterDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    // --- Helper factories ---

    private fun seriesEntity(
        sourceId: Long = 1L,
        url: String = "https://example.com/series/1",
    ) = SeriesEntity(
        sourceId = sourceId,
        url = url,
        title = "Test Series",
        genresJson = "[]",
        status = "ONGOING",
        type = "NOVEL",
    )

    private fun chapterEntity(
        sourceId: Long = 1L,
        url: String = "https://example.com/series/1/ch/1",
        seriesUrl: String = "https://example.com/series/1",
        name: String = "Chapter 1",
        number: Float = 1f,
        read: Boolean = false,
        progress: Float = 0f,
        downloaded: Boolean = false,
    ) = ChapterEntity(
        sourceId = sourceId,
        url = url,
        seriesUrl = seriesUrl,
        name = name,
        number = number,
        read = read,
        progress = progress,
        downloaded = downloaded,
    )

    private suspend fun insertSeriesAndChapters() {
        database.seriesDao().upsert(seriesEntity())
        dao.upsertAll(listOf(
            chapterEntity(url = "https://example.com/series/1/ch/1", name = "Chapter 1", number = 1f),
            chapterEntity(url = "https://example.com/series/1/ch/2", name = "Chapter 2", number = 2f),
            chapterEntity(url = "https://example.com/series/1/ch/3", name = "Chapter 3", number = 3f),
        ))
    }

    // --- observeChapters ---

    @Test
    fun observeChapters_returnsChaptersForSeries() = runTest {
        insertSeriesAndChapters()

        dao.observeChapters(sourceId = 1L, seriesUrl = "https://example.com/series/1").test {
            val items = awaitItem()
            assertThat(items).hasSize(3)
        }
    }

    @Test
    fun observeChapters_ordersByNumberAscending() = runTest {
        insertSeriesAndChapters()

        dao.observeChapters(sourceId = 1L, seriesUrl = "https://example.com/series/1").test {
            val items = awaitItem()
            assertThat(items[0].number).isEqualTo(1f)
            assertThat(items[1].number).isEqualTo(2f)
            assertThat(items[2].number).isEqualTo(3f)
        }
    }

    @Test
    fun observeChapters_filtersBySourceIdAndSeriesUrl() = runTest {
        database.seriesDao().upsert(seriesEntity(sourceId = 1L, url = "https://example.com/series/1"))
        database.seriesDao().upsert(seriesEntity(sourceId = 2L, url = "https://example.com/series/2"))
        dao.upsertAll(listOf(
            chapterEntity(sourceId = 1L, url = "https://example.com/series/1/ch/1", seriesUrl = "https://example.com/series/1"),
            chapterEntity(sourceId = 2L, url = "https://example.com/series/2/ch/1", seriesUrl = "https://example.com/series/2"),
        ))

        dao.observeChapters(sourceId = 1L, seriesUrl = "https://example.com/series/1").test {
            val items = awaitItem()
            assertThat(items).hasSize(1)
            assertThat(items[0].sourceId).isEqualTo(1L)
        }
    }

    @Test
    fun observeChapters_emitsUpdateWhenChapterModified() = runTest {
        insertSeriesAndChapters()

        dao.observeChapters(sourceId = 1L, seriesUrl = "https://example.com/series/1").test {
            awaitItem()

            dao.markRead(sourceId = 1L, url = "https://example.com/series/1/ch/1", read = true)

            val updated = awaitItem()
            assertThat(updated.first { it.url == "https://example.com/series/1/ch/1" }.read).isTrue()
        }
    }

    // --- upsertAll ---

    @Test
    fun upsertAll_insertsNewChapters() = runTest {
        database.seriesDao().upsert(seriesEntity())
        dao.upsertAll(listOf(
            chapterEntity(url = "https://example.com/series/1/ch/1", name = "Chapter 1"),
        ))

        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/1")).isNotNull()
    }

    @Test
    fun upsertAll_replacesExistingChapters() = runTest {
        database.seriesDao().upsert(seriesEntity())
        val original = chapterEntity(url = "https://example.com/series/1/ch/1", name = "Chapter 1")
        dao.upsertAll(listOf(original))

        val updated = original.copy(name = "Chapter 1: Revised")
        dao.upsertAll(listOf(updated))

        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/1")!!.name).isEqualTo("Chapter 1: Revised")
    }

    // --- markRead ---

    @Test
    fun markRead_setsReadTrue() = runTest {
        insertSeriesAndChapters()

        dao.markRead(sourceId = 1L, url = "https://example.com/series/1/ch/1", read = true)

        val chapter = dao.getByUrl(1L, "https://example.com/series/1/ch/1")
        assertThat(chapter!!.read).isTrue()
    }

    @Test
    fun markRead_setsReadFalse() = runTest {
        insertSeriesAndChapters()
        dao.markRead(sourceId = 1L, url = "https://example.com/series/1/ch/1", read = true)
        dao.markRead(sourceId = 1L, url = "https://example.com/series/1/ch/1", read = false)

        val chapter = dao.getByUrl(1L, "https://example.com/series/1/ch/1")
        assertThat(chapter!!.read).isFalse()
    }

    // --- setProgress ---

    @Test
    fun setProgress_updatesProgressValue() = runTest {
        insertSeriesAndChapters()

        dao.setProgress(sourceId = 1L, url = "https://example.com/series/1/ch/1", progress = 0.5f)

        val chapter = dao.getByUrl(1L, "https://example.com/series/1/ch/1")
        assertThat(chapter!!.progress).isEqualTo(0.5f)
    }

    @Test
    fun setProgress_doesNotAffectOtherFields() = runTest {
        insertSeriesAndChapters()
        dao.markRead(sourceId = 1L, url = "https://example.com/series/1/ch/1", read = true)
        dao.setProgress(sourceId = 1L, url = "https://example.com/series/1/ch/1", progress = 0.75f)

        val chapter = dao.getByUrl(1L, "https://example.com/series/1/ch/1")
        assertThat(chapter!!.read).isTrue()
        assertThat(chapter.progress).isEqualTo(0.75f)
    }

    // --- deleteBySeries ---

    @Test
    fun deleteBySeries_removesAllChaptersForSeries() = runTest {
        insertSeriesAndChapters()

        dao.deleteBySeries(sourceId = 1L, seriesUrl = "https://example.com/series/1")

        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/1")).isNull()
        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/2")).isNull()
        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/3")).isNull()
    }

    @Test
    fun deleteBySeries_doesNotAffectOtherSeries() = runTest {
        database.seriesDao().upsert(seriesEntity(sourceId = 1L, url = "https://example.com/series/1"))
        database.seriesDao().upsert(seriesEntity(sourceId = 2L, url = "https://example.com/series/2"))
        dao.upsertAll(listOf(
            chapterEntity(sourceId = 1L, url = "https://example.com/series/1/ch/1", seriesUrl = "https://example.com/series/1"),
            chapterEntity(sourceId = 2L, url = "https://example.com/series/2/ch/1", seriesUrl = "https://example.com/series/2"),
        ))

        dao.deleteBySeries(sourceId = 1L, seriesUrl = "https://example.com/series/1")

        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/1")).isNull()
        assertThat(dao.getByUrl(2L, "https://example.com/series/2/ch/1")).isNotNull()
    }

    // --- deleteChapters ---

    @Test
    fun deleteChapters_removesSpecifiedChapters() = runTest {
        insertSeriesAndChapters()

        val ch2 = dao.getByUrl(1L, "https://example.com/series/1/ch/2")!!
        dao.deleteChapters(listOf(ch2))

        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/1")).isNotNull()
        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/2")).isNull()
        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/3")).isNotNull()
    }

    // --- replaceChaptersForSeries ---

    @Test
    fun replaceChaptersForSeries_preservesReadProgressAndDownloadedState() = runTest {
        database.seriesDao().upsert(seriesEntity())
        dao.upsertAll(listOf(
            chapterEntity(
                url = "https://example.com/series/1/ch/1",
                name = "Chapter 1",
                read = true,
                progress = 0.5f,
                downloaded = true,
            ),
        ))

        val remoteChapter = chapterEntity(
            url = "https://example.com/series/1/ch/1",
            name = "Chapter 1: Updated Title",
            read = false,
            progress = 0f,
            downloaded = false,
        )

        dao.replaceChaptersForSeries(
            sourceId = 1L,
            seriesUrl = "https://example.com/series/1",
            remoteChapters = listOf(remoteChapter),
        )

        val chapter = dao.getByUrl(1L, "https://example.com/series/1/ch/1")!!
        assertThat(chapter.name).isEqualTo("Chapter 1: Updated Title")
        assertThat(chapter.read).isTrue()
        assertThat(chapter.progress).isEqualTo(0.5f)
        assertThat(chapter.downloaded).isTrue()
    }

    @Test
    fun replaceChaptersForSeries_deletesOnlyAbsentChapters() = runTest {
        insertSeriesAndChapters()
        database.seriesDao().upsert(seriesEntity(sourceId = 2L, url = "https://example.com/series/2"))
        dao.upsertAll(listOf(
            chapterEntity(sourceId = 2L, url = "https://example.com/series/2/ch/1", seriesUrl = "https://example.com/series/2"),
        ))

        val remoteChapters = listOf(
            chapterEntity(url = "https://example.com/series/1/ch/1", name = "Chapter 1"),
            chapterEntity(url = "https://example.com/series/1/ch/3", name = "Chapter 3"),
        )

        dao.replaceChaptersForSeries(
            sourceId = 1L,
            seriesUrl = "https://example.com/series/1",
            remoteChapters = remoteChapters,
        )

        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/1")).isNotNull()
        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/2")).isNull()
        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/3")).isNotNull()
        assertThat(dao.getByUrl(2L, "https://example.com/series/2/ch/1")).isNotNull()
    }

    @Test
    fun replaceChaptersForSeries_insertsNewChaptersWithDefaultState() = runTest {
        database.seriesDao().upsert(seriesEntity())

        val remoteChapters = listOf(
            chapterEntity(
                url = "https://example.com/series/1/ch/new",
                name = "New Chapter",
                read = false,
                progress = 0f,
                downloaded = false,
            ),
        )

        dao.replaceChaptersForSeries(
            sourceId = 1L,
            seriesUrl = "https://example.com/series/1",
            remoteChapters = remoteChapters,
        )

        val chapter = dao.getByUrl(1L, "https://example.com/series/1/ch/new")
        assertThat(chapter).isNotNull()
        assertThat(chapter!!.read).isFalse()
        assertThat(chapter.progress).isEqualTo(0f)
        assertThat(chapter.downloaded).isFalse()
    }

    @Test
    fun replaceChaptersForSeries_preservesStateWhenUrlsRotateWithUniqueNonNegativeNumber() = runTest {
        database.seriesDao().upsert(seriesEntity())
        dao.upsertAll(
            listOf(
                chapterEntity(
                    url = "https://example.com/series/1/ch/old-1",
                    number = 1f,
                    read = true,
                    progress = 0.6f,
                    downloaded = true,
                ),
            ),
        )

        val remoteChapters = listOf(
            chapterEntity(
                url = "https://example.com/series/1/ch/new-1",
                number = 1f,
                read = false,
                progress = 0f,
                downloaded = false,
            ),
        )

        dao.replaceChaptersForSeries(
            sourceId = 1L,
            seriesUrl = "https://example.com/series/1",
            remoteChapters = remoteChapters,
        )

        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/old-1")).isNull()
        val newChapter = dao.getByUrl(1L, "https://example.com/series/1/ch/new-1")
        assertThat(newChapter).isNotNull()
        assertThat(newChapter!!.read).isTrue()
        assertThat(newChapter.progress).isEqualTo(0.6f)
        assertThat(newChapter.downloaded).isTrue()
    }

    @Test
    fun replaceChaptersForSeries_doesNotMatchByNumberWhenNegativeOrDuplicate() = runTest {
        database.seriesDao().upsert(seriesEntity())
        dao.upsertAll(
            listOf(
                chapterEntity(
                    url = "https://example.com/series/1/ch/old-neg",
                    number = -1f,
                    read = true,
                    progress = 0.8f,
                    downloaded = true,
                ),
                chapterEntity(
                    url = "https://example.com/series/1/ch/old-dup-5",
                    number = 5f,
                    read = true,
                    progress = 0.9f,
                    downloaded = true,
                ),
                chapterEntity(
                    url = "https://example.com/series/1/ch/old-dup-6a",
                    number = 6f,
                    read = true,
                    progress = 0.4f,
                    downloaded = true,
                ),
                chapterEntity(
                    url = "https://example.com/series/1/ch/old-dup-6b",
                    number = 6f,
                    read = true,
                    progress = 0.7f,
                    downloaded = false,
                ),
            ),
        )

        val remoteChapters = listOf(
            chapterEntity(
                url = "https://example.com/series/1/ch/new-neg",
                number = -1f,
                read = false,
                progress = 0f,
                downloaded = false,
            ),
            chapterEntity(
                url = "https://example.com/series/1/ch/new-dup-5a",
                number = 5f,
                read = false,
                progress = 0f,
                downloaded = false,
            ),
            chapterEntity(
                url = "https://example.com/series/1/ch/new-dup-5b",
                number = 5f,
                read = false,
                progress = 0f,
                downloaded = false,
            ),
            chapterEntity(
                url = "https://example.com/series/1/ch/new-dup-6",
                number = 6f,
                read = false,
                progress = 0f,
                downloaded = false,
            ),
        )

        dao.replaceChaptersForSeries(
            sourceId = 1L,
            seriesUrl = "https://example.com/series/1",
            remoteChapters = remoteChapters,
        )

        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/old-neg")).isNull()
        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/old-dup-5")).isNull()
        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/old-dup-6a")).isNull()
        assertThat(dao.getByUrl(1L, "https://example.com/series/1/ch/old-dup-6b")).isNull()

        val newNeg = dao.getByUrl(1L, "https://example.com/series/1/ch/new-neg")
        assertThat(newNeg).isNotNull()
        assertThat(newNeg!!.read).isFalse()
        assertThat(newNeg.progress).isEqualTo(0f)
        assertThat(newNeg.downloaded).isFalse()

        val newDup5a = dao.getByUrl(1L, "https://example.com/series/1/ch/new-dup-5a")
        assertThat(newDup5a).isNotNull()
        assertThat(newDup5a!!.read).isFalse()
        assertThat(newDup5a.progress).isEqualTo(0f)
        assertThat(newDup5a.downloaded).isFalse()

        val newDup5b = dao.getByUrl(1L, "https://example.com/series/1/ch/new-dup-5b")
        assertThat(newDup5b).isNotNull()
        assertThat(newDup5b!!.read).isFalse()
        assertThat(newDup5b.progress).isEqualTo(0f)
        assertThat(newDup5b.downloaded).isFalse()

        val newDup6 = dao.getByUrl(1L, "https://example.com/series/1/ch/new-dup-6")
        assertThat(newDup6).isNotNull()
        assertThat(newDup6!!.read).isFalse()
        assertThat(newDup6.progress).isEqualTo(0f)
        assertThat(newDup6.downloaded).isFalse()
    }

    @Test
    fun replaceChaptersForSeries_doesNotDeleteOrResetWhenRemoteChaptersIsEmpty() = runTest {
        database.seriesDao().upsert(seriesEntity())
        dao.upsertAll(
            listOf(
                chapterEntity(
                    url = "https://example.com/series/1/ch/1",
                    number = 1f,
                    read = true,
                    progress = 0.5f,
                    downloaded = true,
                ),
            ),
        )

        dao.replaceChaptersForSeries(
            sourceId = 1L,
            seriesUrl = "https://example.com/series/1",
            remoteChapters = emptyList(),
        )

        val chapter = dao.getByUrl(1L, "https://example.com/series/1/ch/1")
        assertThat(chapter).isNotNull()
        assertThat(chapter!!.read).isTrue()
        assertThat(chapter.progress).isEqualTo(0.5f)
        assertThat(chapter.downloaded).isTrue()
    }
}