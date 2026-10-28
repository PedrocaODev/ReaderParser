package com.opus.readerparser.domain.model

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val novelFontSize: Int = 18,
    val novelFontFamily: String = "Serif",
    val novelLineHeightMultiplier: Float = 1.6f,
    val novelParagraphSpacingDp: Int = 14,
    val novelHorizontalPaddingDp: Int = 20,
    val manhwaLayout: ManhwaLayout = ManhwaLayout.WEBTOON,
    val manhwaZoom: ManhwaZoom = ManhwaZoom.FIT_WIDTH,
) {
    companion object {
        val NOVEL_FONT_SIZE_RANGE: IntRange = 12..32
        val NOVEL_LINE_HEIGHT_RANGE: ClosedFloatingPointRange<Float> = 1.2f..2.0f
        val NOVEL_PARAGRAPH_SPACING_RANGE: IntRange = 4..28
        val NOVEL_HORIZONTAL_PADDING_RANGE: IntRange = 8..40
    }
}
