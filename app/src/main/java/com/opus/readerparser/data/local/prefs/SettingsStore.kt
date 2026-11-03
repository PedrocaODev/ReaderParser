package com.opus.readerparser.data.local.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.opus.readerparser.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val data: Flow<Preferences> get() = dataStore.data

    suspend fun edit(transform: suspend (MutablePreferences) -> Unit): Preferences =
        dataStore.edit(transform)

    suspend fun setNovelFontSize(size: Int) {
        edit { it[Keys.NOVEL_FONT_SIZE] = size.coerceIn(AppSettings.NOVEL_FONT_SIZE_RANGE) }
    }

    suspend fun setNovelFontFamily(family: String) {
        edit { it[Keys.NOVEL_FONT_FAMILY] = family }
    }

    suspend fun setNovelLineHeightMultiplier(multiplier: Float) {
        edit { it[Keys.NOVEL_LINE_HEIGHT_MULTIPLIER] = multiplier.coerceIn(AppSettings.NOVEL_LINE_HEIGHT_RANGE) }
    }

    suspend fun setNovelParagraphSpacingDp(dp: Int) {
        edit { it[Keys.NOVEL_PARAGRAPH_SPACING_DP] = dp.coerceIn(AppSettings.NOVEL_PARAGRAPH_SPACING_RANGE) }
    }

    suspend fun setNovelHorizontalPaddingDp(dp: Int) {
        edit { it[Keys.NOVEL_HORIZONTAL_PADDING_DP] = dp.coerceIn(AppSettings.NOVEL_HORIZONTAL_PADDING_RANGE) }
    }

    object Keys {
        val THEME = stringPreferencesKey("theme")
        val NOVEL_FONT_SIZE = intPreferencesKey("novel_font_size")
        val NOVEL_FONT_FAMILY = stringPreferencesKey("novel_font_family")
        val NOVEL_LINE_HEIGHT_MULTIPLIER = floatPreferencesKey("novel_line_height_multiplier")
        val NOVEL_PARAGRAPH_SPACING_DP = intPreferencesKey("novel_paragraph_spacing_dp")
        val NOVEL_HORIZONTAL_PADDING_DP = intPreferencesKey("novel_horizontal_padding_dp")
        val MANHWA_LAYOUT = stringPreferencesKey("manhwa_layout")
        val MANHWA_ZOOM = stringPreferencesKey("manhwa_zoom")
    }
}
