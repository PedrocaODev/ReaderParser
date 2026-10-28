package com.opus.readerparser.data.local.prefs

import com.opus.readerparser.domain.SettingsRepository
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.AppTheme
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val store: SettingsStore,
) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> =
        store.data.map { prefs ->
            AppSettings(
                theme = prefs[SettingsStore.Keys.THEME]?.let { AppTheme.valueOf(it) } ?: AppTheme.SYSTEM,
                novelFontSize = (prefs[SettingsStore.Keys.NOVEL_FONT_SIZE] ?: 18).coerceIn(AppSettings.NOVEL_FONT_SIZE_RANGE),
                novelFontFamily = prefs[SettingsStore.Keys.NOVEL_FONT_FAMILY] ?: "Serif",
                novelLineHeightMultiplier = (prefs[SettingsStore.Keys.NOVEL_LINE_HEIGHT_MULTIPLIER] ?: 1.6f).coerceIn(AppSettings.NOVEL_LINE_HEIGHT_RANGE),
                novelParagraphSpacingDp = (prefs[SettingsStore.Keys.NOVEL_PARAGRAPH_SPACING_DP] ?: 14).coerceIn(AppSettings.NOVEL_PARAGRAPH_SPACING_RANGE),
                novelHorizontalPaddingDp = (prefs[SettingsStore.Keys.NOVEL_HORIZONTAL_PADDING_DP] ?: 20).coerceIn(AppSettings.NOVEL_HORIZONTAL_PADDING_RANGE),
                manhwaLayout = prefs[SettingsStore.Keys.MANHWA_LAYOUT]?.let { ManhwaLayout.valueOf(it) } ?: ManhwaLayout.WEBTOON,
                manhwaZoom = prefs[SettingsStore.Keys.MANHWA_ZOOM]?.let { ManhwaZoom.valueOf(it) } ?: ManhwaZoom.FIT_WIDTH,
            )
        }

    override suspend fun setTheme(theme: AppTheme) {
        store.edit { it[SettingsStore.Keys.THEME] = theme.name }
    }

    override suspend fun setNovelFontSize(size: Int) {
        store.setNovelFontSize(size)
    }

    override suspend fun setNovelFontFamily(family: String) {
        store.setNovelFontFamily(family)
    }

    override suspend fun setNovelLineHeightMultiplier(multiplier: Float) {
        store.setNovelLineHeightMultiplier(multiplier)
    }

    override suspend fun setNovelParagraphSpacingDp(dp: Int) {
        store.setNovelParagraphSpacingDp(dp)
    }

    override suspend fun setNovelHorizontalPaddingDp(dp: Int) {
        store.setNovelHorizontalPaddingDp(dp)
    }

    override suspend fun setManhwaLayout(layout: ManhwaLayout) {
        store.edit { it[SettingsStore.Keys.MANHWA_LAYOUT] = layout.name }
    }

    override suspend fun setManhwaZoom(zoom: ManhwaZoom) {
        store.edit { it[SettingsStore.Keys.MANHWA_ZOOM] = zoom.name }
    }
}
