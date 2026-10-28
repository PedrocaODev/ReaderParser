package com.opus.readerparser.ui.reader

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NovelHtmlParserTest {

    @Test
    fun `parse headings h1 to h6 with correct levels and text`() {
        val html = """
            <h1>Title One</h1>
            <h2>Title Two</h2>
            <h3>Title Three</h3>
            <h4>Title Four</h4>
            <h5>Title Five</h5>
            <h6>Title Six</h6>
        """.trimIndent()

        val blocks = parseNovelHtml(html)
        assertEquals(6, blocks.size)
        for (i in 1..6) {
            val heading = blocks[i - 1] as NovelBlock.Heading
            assertEquals(i, heading.level)
            assertTrue(heading.text.text.startsWith("Title"))
        }
        assertEquals("Title One", (blocks[0] as NovelBlock.Heading).text.text)
        assertEquals("Title Six", (blocks[5] as NovelBlock.Heading).text.text)
    }

    @Test
    fun `parse paragraphs, blockquotes, and list items into Paragraph blocks`() {
        val html = """
            <p>Standard paragraph</p>
            <blockquote>Quoted excerpt</blockquote>
            <ul>
                <li>First bullet</li>
                <li>Second bullet</li>
            </ul>
        """.trimIndent()

        val blocks = parseNovelHtml(html)
        assertEquals(4, blocks.size)
        assertEquals("Standard paragraph", (blocks[0] as NovelBlock.Paragraph).text.text)
        assertEquals("Quoted excerpt", (blocks[1] as NovelBlock.Paragraph).text.text)
        assertEquals("First bullet", (blocks[2] as NovelBlock.Paragraph).text.text)
        assertEquals("Second bullet", (blocks[3] as NovelBlock.Paragraph).text.text)
    }

    @Test
    fun `parse inline styles bold, italic, strikethrough, and underline`() {
        val html = "<p><b>Bold</b> <strong>Strong</strong> <i>Italic</i> <em>Em</em> <s>Strike</s> <del>Del</del> <strike>OldStrike</strike> <u>Underline</u></p>"
        val blocks = parseNovelHtml(html)
        assertEquals(1, blocks.size)
        val p = blocks[0] as NovelBlock.Paragraph
        assertEquals("Bold Strong Italic Em Strike Del OldStrike Underline", p.text.text)

        val styles = p.text.spanStyles
        assertTrue(styles.any { it.item.fontWeight == FontWeight.Bold })
        assertTrue(styles.any { it.item.fontStyle == FontStyle.Italic })
        assertTrue(styles.any { it.item.textDecoration == TextDecoration.LineThrough })
        assertTrue(styles.any { it.item.textDecoration == TextDecoration.Underline })
    }

    @Test
    fun `parse hr tags and scene break text into Divider blocks`() {
        val html = """
            <p>Before break</p>
            <hr>
            <p>Between breaks</p>
            <p>***</p>
            <p>* * *</p>
            <p>---</p>
            <p>— — —</p>
            <p>___</p>
            <p>After break</p>
        """.trimIndent()

        val blocks = parseNovelHtml(html)
        assertEquals(9, blocks.size)
        assertTrue(blocks[0] is NovelBlock.Paragraph)
        assertTrue(blocks[1] is NovelBlock.Divider)
        assertTrue(blocks[2] is NovelBlock.Paragraph)
        assertTrue(blocks[3] is NovelBlock.Divider)
        assertTrue(blocks[4] is NovelBlock.Divider)
        assertTrue(blocks[5] is NovelBlock.Divider)
        assertTrue(blocks[6] is NovelBlock.Divider)
        assertTrue(blocks[7] is NovelBlock.Divider)
        assertTrue(blocks[8] is NovelBlock.Paragraph)
    }

    @Test
    fun `fallback parsing for plain text with double newlines or br tags`() {
        val plainTextHtml = "First section of story.\n\nSecond section with details.<br><br>Third section follows."
        val blocks = parseNovelHtml(plainTextHtml)
        assertEquals(3, blocks.size)
        assertEquals("First section of story.", (blocks[0] as NovelBlock.Paragraph).text.text)
        assertEquals("Second section with details.", (blocks[1] as NovelBlock.Paragraph).text.text)
        assertEquals("Third section follows.", (blocks[2] as NovelBlock.Paragraph).text.text)
    }

    @Test
    fun `empty and blank html returns empty list`() {
        assertTrue(parseNovelHtml("").isEmpty())
        assertTrue(parseNovelHtml("   \n\t  ").isEmpty())
    }

    @Test
    fun `resolveNovelFontFamily maps known names and defaults to Serif`() {
        assertEquals(FontFamily.Serif, resolveNovelFontFamily("Serif"))
        assertEquals(FontFamily.Serif, resolveNovelFontFamily("serif"))
        assertEquals(FontFamily.SansSerif, resolveNovelFontFamily("Sans-Serif"))
        assertEquals(FontFamily.SansSerif, resolveNovelFontFamily("sans"))
        assertEquals(FontFamily.Monospace, resolveNovelFontFamily("Monospace"))
        assertEquals(FontFamily.Monospace, resolveNovelFontFamily("mono"))
        assertEquals(FontFamily.Default, resolveNovelFontFamily("Default"))
        assertEquals(FontFamily.Default, resolveNovelFontFamily("default"))
        assertEquals(FontFamily.Serif, resolveNovelFontFamily("UnknownFamily"))
    }

    @Test
    fun `targetBlockIndexForProgress calculates correct clamped index`() {
        assertEquals(0, targetBlockIndexForProgress(0f, 10))
        assertEquals(0, targetBlockIndexForProgress(-0.5f, 10))
        assertEquals(9, targetBlockIndexForProgress(1f, 10))
        assertEquals(9, targetBlockIndexForProgress(1.5f, 10))
        assertEquals(5, targetBlockIndexForProgress(0.5f, 11)) // 0.5 * 10 = 5
        assertEquals(0, targetBlockIndexForProgress(0.5f, 0))
    }

    @Test
    fun `calculateNovelScrollProgress handles start, fractional progress, and end reached`() {
        // At the very top
        val topProgress = calculateNovelScrollProgress(
            firstVisibleIndex = 0,
            firstVisibleOffset = 0,
            firstVisibleSize = 100,
            lastVisibleIndex = 2,
            lastVisibleBottomReached = false,
            totalBlocks = 10,
        )
        assertEquals(0f, topProgress, 0.001f)

        // Midway with offset
        val midProgress = calculateNovelScrollProgress(
            firstVisibleIndex = 4,
            firstVisibleOffset = 50,
            firstVisibleSize = 100,
            lastVisibleIndex = 6,
            lastVisibleBottomReached = false,
            totalBlocks = 10,
        )
        // (4 + 0.5) / 9 = 4.5 / 9 = 0.5f
        assertEquals(0.5f, midProgress, 0.001f)

        // Bottom reached completes progress
        val endProgress = calculateNovelScrollProgress(
            firstVisibleIndex = 8,
            firstVisibleOffset = 0,
            firstVisibleSize = 100,
            lastVisibleIndex = 9,
            lastVisibleBottomReached = true,
            totalBlocks = 10,
        )
        assertEquals(1.0f, endProgress, 0.001f)

        // Single block
        val singleBlockProgress = calculateNovelScrollProgress(
            firstVisibleIndex = 0,
            firstVisibleOffset = 0,
            firstVisibleSize = 100,
            lastVisibleIndex = 0,
            lastVisibleBottomReached = false,
            totalBlocks = 1,
        )
        assertEquals(0f, singleBlockProgress, 0.001f)
    }
}
