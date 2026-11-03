package com.opus.readerparser.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.opus.readerparser.domain.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * JVM tests for [SettingsStore] and [SettingsRepositoryImpl].
 *
 * Uses a hand-rolled [FakeDataStore] to verify that [SettingsStore] correctly
 * delegates [data] and [edit] to the underlying [DataStore] without any Android
 * dependency.
 */
class SettingsStoreTest {

    // ---- fake ---------------------------------------------------------------

    private class FakeDataStore : DataStore<Preferences> {
        private var current: Preferences = mutablePreferencesOf()

        override val data: Flow<Preferences>
            get() = flowOf(current)

        override suspend fun updateData(
            transform: suspend (t: Preferences) -> Preferences,
        ): Preferences {
            current = transform(current)
            return current
        }
    }

    // Adapter so tests can call store.edit { } the same way production code does.
    // DataStore<Preferences>.edit is an extension function that calls updateData internally.
    // We expose it via SettingsStore.edit which calls dataStore.edit.
    // The real DataStore.edit extension uses MutablePreferences; our fake needs to support it.
    // We implement a minimal stand-in by overriding updateData on the fake.
    private class EditableFakeDataStore : DataStore<Preferences> {
        private var current: Preferences = mutablePreferencesOf()

        override val data: Flow<Preferences>
            get() = flowOf(current)

        override suspend fun updateData(
            transform: suspend (t: Preferences) -> Preferences,
        ): Preferences {
            val mutable = current.toMutablePreferences()
            current = transform(mutable)
            return current
        }
    }

    // ---- tests --------------------------------------------------------------

    @Test
    fun `data delegates to underlying DataStore`() = runTest {
        val fakeDataStore = FakeDataStore()
        val store = SettingsStore(fakeDataStore)

        val prefs = store.data.first()
        assertEquals(fakeDataStore.data.first(), prefs)
    }

    @Test
    fun `edit updates underlying DataStore`() = runTest {
        val fakeDataStore = EditableFakeDataStore()
        val store = SettingsStore(fakeDataStore)

        val key = stringPreferencesKey("test_key")
        store.edit { it[key] = "hello" }

        val prefs = store.data.first()
        assertEquals("hello", prefs[key])
    }

    @Test
    fun `edit can overwrite an existing preference`() = runTest {
        val fakeDataStore = EditableFakeDataStore()
        val store = SettingsStore(fakeDataStore)

        val key = stringPreferencesKey("theme")
        store.edit { it[key] = "DARK" }
        store.edit { it[key] = "LIGHT" }

        assertEquals("LIGHT", store.data.first()[key])
    }

    @Test
    fun `setTheme WARM_DARK round-trips through SettingsRepositoryImpl`() = runTest {
        val fakeDataStore = EditableFakeDataStore()
        val store = SettingsStore(fakeDataStore)
        val repo = SettingsRepositoryImpl(store)

        repo.setTheme(AppTheme.WARM_DARK)
        assertEquals(AppTheme.WARM_DARK, repo.observeSettings().first().theme)
    }

    @Test
    fun `setTheme SEPIA round-trips through SettingsRepositoryImpl`() = runTest {
        val fakeDataStore = EditableFakeDataStore()
        val store = SettingsStore(fakeDataStore)
        val repo = SettingsRepositoryImpl(store)

        repo.setTheme(AppTheme.SEPIA)
        assertEquals(AppTheme.SEPIA, repo.observeSettings().first().theme)
    }

    @Test
    fun `default AppSettings values from SettingsRepositoryImpl match Phase 3 typography spec`() = runTest {
        val fakeDataStore = EditableFakeDataStore()
        val store = SettingsStore(fakeDataStore)
        val repo = SettingsRepositoryImpl(store)

        val settings = repo.observeSettings().first()
        assertEquals(18, settings.novelFontSize)
        assertEquals("Serif", settings.novelFontFamily)
        assertEquals(1.6f, settings.novelLineHeightMultiplier, 0.001f)
        assertEquals(14, settings.novelParagraphSpacingDp)
        assertEquals(20, settings.novelHorizontalPaddingDp)
    }

    @Test
    fun `novel typography preferences round-trip through SettingsRepositoryImpl`() = runTest {
        val fakeDataStore = EditableFakeDataStore()
        val store = SettingsStore(fakeDataStore)
        val repo = SettingsRepositoryImpl(store)

        repo.setNovelLineHeightMultiplier(1.8f)
        repo.setNovelParagraphSpacingDp(20)
        repo.setNovelHorizontalPaddingDp(28)

        val settings = repo.observeSettings().first()
        assertEquals(1.8f, settings.novelLineHeightMultiplier, 0.001f)
        assertEquals(20, settings.novelParagraphSpacingDp)
        assertEquals(28, settings.novelHorizontalPaddingDp)
    }

    @Test
    fun `novel typography preferences clamp out-of-range values in SettingsRepositoryImpl`() = runTest {
        val fakeDataStore = EditableFakeDataStore()
        val store = SettingsStore(fakeDataStore)
        val repo = SettingsRepositoryImpl(store)

        // Below range clamping
        repo.setNovelFontSize(5)
        repo.setNovelLineHeightMultiplier(0.5f)
        repo.setNovelParagraphSpacingDp(1)
        repo.setNovelHorizontalPaddingDp(2)

        var settings = repo.observeSettings().first()
        assertEquals(12, settings.novelFontSize)
        assertEquals(1.2f, settings.novelLineHeightMultiplier, 0.001f)
        assertEquals(4, settings.novelParagraphSpacingDp)
        assertEquals(8, settings.novelHorizontalPaddingDp)

        // Above range clamping
        repo.setNovelFontSize(50)
        repo.setNovelLineHeightMultiplier(3.5f)
        repo.setNovelParagraphSpacingDp(50)
        repo.setNovelHorizontalPaddingDp(100)

        settings = repo.observeSettings().first()
        assertEquals(32, settings.novelFontSize)
        assertEquals(2.0f, settings.novelLineHeightMultiplier, 0.001f)
        assertEquals(28, settings.novelParagraphSpacingDp)
        assertEquals(40, settings.novelHorizontalPaddingDp)
    }

    @Test
    fun `SettingsStore direct setter methods clamp out-of-range values`() = runTest {
        val fakeDataStore = EditableFakeDataStore()
        val store = SettingsStore(fakeDataStore)

        store.setNovelFontSize(99)
        store.setNovelLineHeightMultiplier(99f)
        store.setNovelParagraphSpacingDp(99)
        store.setNovelHorizontalPaddingDp(99)

        val prefs = store.data.first()
        assertEquals(32, prefs[SettingsStore.Keys.NOVEL_FONT_SIZE])
        assertEquals(2.0f, prefs[SettingsStore.Keys.NOVEL_LINE_HEIGHT_MULTIPLIER])
        assertEquals(28, prefs[SettingsStore.Keys.NOVEL_PARAGRAPH_SPACING_DP])
        assertEquals(40, prefs[SettingsStore.Keys.NOVEL_HORIZONTAL_PADDING_DP])
    }
}
