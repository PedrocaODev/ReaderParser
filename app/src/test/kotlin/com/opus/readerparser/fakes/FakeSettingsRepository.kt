package com.opus.readerparser.fakes

import com.opus.readerparser.domain.SettingsRepository
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.AppTheme
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository : SettingsRepository {

    private val _settings = MutableStateFlow(AppSettings())

    override fun observeSettings(): Flow<AppSettings> = _settings

    override suspend fun setTheme(theme: AppTheme) {
        _settings.value = _settings.value.copy(theme = theme)
    }

    override suspend fun setNovelFontSize(size: Int) {
        _settings.value = _settings.value.copy(
            novelFontSize = size.coerceIn(AppSettings.NOVEL_FONT_SIZE_RANGE),
        )
    }

    override suspend fun setNovelFontFamily(family: String) {
        _settings.value = _settings.value.copy(novelFontFamily = family)
    }

    override suspend fun setNovelLineHeightMultiplier(multiplier: Float) {
        _settings.value = _settings.value.copy(
            novelLineHeightMultiplier = multiplier.coerceIn(AppSettings.NOVEL_LINE_HEIGHT_RANGE),
        )
    }

    override suspend fun setNovelParagraphSpacingDp(dp: Int) {
        _settings.value = _settings.value.copy(
            novelParagraphSpacingDp = dp.coerceIn(AppSettings.NOVEL_PARAGRAPH_SPACING_RANGE),
        )
    }

    override suspend fun setNovelHorizontalPaddingDp(dp: Int) {
        _settings.value = _settings.value.copy(
            novelHorizontalPaddingDp = dp.coerceIn(AppSettings.NOVEL_HORIZONTAL_PADDING_RANGE),
        )
    }

    override suspend fun setManhwaLayout(layout: ManhwaLayout) {
        _settings.value = _settings.value.copy(manhwaLayout = layout)
    }

    override suspend fun setManhwaZoom(zoom: ManhwaZoom) {
        _settings.value = _settings.value.copy(manhwaZoom = zoom)
    }
}
