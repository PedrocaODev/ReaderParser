package com.opus.readerparser.ui.reader

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import kotlin.math.roundToInt

sealed interface NovelBlock {
    data class Heading(val text: AnnotatedString, val level: Int) : NovelBlock
    data class Paragraph(val text: AnnotatedString) : NovelBlock
    data object Divider : NovelBlock
}

fun parseNovelHtml(html: String): List<NovelBlock> = NovelHtmlParser.parse(html)

fun resolveNovelFontFamily(family: String): FontFamily =
    when (family.trim().lowercase()) {
        "sans-serif", "sans" -> FontFamily.SansSerif
        "monospace", "mono" -> FontFamily.Monospace
        "default" -> FontFamily.Default
        "serif" -> FontFamily.Serif
        else -> FontFamily.Serif
    }

fun targetBlockIndexForProgress(progress: Float, blockCount: Int): Int {
    if (blockCount <= 0) return 0
    return (progress.coerceIn(0f, 1f) * (blockCount - 1)).roundToInt().coerceIn(0, blockCount - 1)
}

fun calculateNovelScrollProgress(
    firstVisibleIndex: Int,
    firstVisibleOffset: Int,
    firstVisibleSize: Int,
    lastVisibleIndex: Int,
    lastVisibleBottomReached: Boolean,
    totalBlocks: Int,
): Float {
    if (totalBlocks <= 0) return 0f
    if (lastVisibleIndex >= totalBlocks - 1 && lastVisibleBottomReached) return 1.0f
    if (totalBlocks == 1) return 0f
    val itemFraction = if (firstVisibleSize > 0) {
        (firstVisibleOffset.toFloat() / firstVisibleSize.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    return ((firstVisibleIndex + itemFraction) / (totalBlocks - 1).toFloat()).coerceIn(0f, 1f)
}

internal fun AnnotatedString.trimWhitespace(): AnnotatedString {
    var start = 0
    while (start < length && text[start].isWhitespace()) {
        start++
    }
    if (start == length) {
        return AnnotatedString("")
    }
    var end = length - 1
    while (end >= start && text[end].isWhitespace()) {
        end--
    }
    return subSequence(start, end + 1)
}

object NovelHtmlParser {
    private val SCENE_BREAK_REGEX = Regex("""^(?:[*—–\-_~]\s*){3,}$""")
    private val DOUBLE_NEWLINE_REGEX = Regex("""\r?\n\s*\r?\n""")
    private val WHITESPACE_COLLAPSE_REGEX = Regex("""[ \t\r\n]+""")

    private val BLOCK_CONTAINER_TAGS = setOf(
        "div", "article", "section", "main", "header", "footer", "aside", "nav",
        "ul", "ol", "table", "tbody", "tr", "td", "th",
    )

    private val PARAGRAPH_TAGS = setOf("p", "blockquote", "li")
    private val HEADING_TAGS = setOf("h1", "h2", "h3", "h4", "h5", "h6")

    internal fun isSceneBreak(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return false
        return SCENE_BREAK_REGEX.matches(trimmed)
    }

    fun parse(html: String): List<NovelBlock> {
        if (html.isBlank()) return emptyList()

        val doc = Jsoup.parseBodyFragment(html)
        doc.select("script, style, noscript").remove()
        val body = doc.body()

        val blocks = mutableListOf<NovelBlock>()
        walkContainer(body, blocks)
        return blocks
    }

    private fun walkContainer(container: Element, outBlocks: MutableList<NovelBlock>) {
        var inlineBuilder = AnnotatedString.Builder()

        fun flushInline() {
            val annotated = inlineBuilder.toAnnotatedString()
            inlineBuilder = AnnotatedString.Builder()
            if (annotated.isEmpty()) return
            val slices = splitByBreak(annotated)
            for (slice in slices) {
                val trimmed = slice.trimWhitespace()
                if (trimmed.text.isNotEmpty()) {
                    if (isSceneBreak(trimmed.text)) {
                        outBlocks.add(NovelBlock.Divider)
                    } else {
                        outBlocks.add(NovelBlock.Paragraph(trimmed))
                    }
                }
            }
        }

        for (child in container.childNodes()) {
            when {
                child is Element && child.normalName() == "hr" -> {
                    flushInline()
                    outBlocks.add(NovelBlock.Divider)
                }
                child is Element && child.normalName() in HEADING_TAGS -> {
                    flushInline()
                    val level = child.normalName().removePrefix("h").toIntOrNull() ?: 1
                    val headingBuilder = AnnotatedString.Builder()
                    appendInlineNode(child, headingBuilder, inContainer = false, isInsideHeading = true)
                    val trimmed = headingBuilder.toAnnotatedString().trimWhitespace()
                    if (trimmed.text.isNotEmpty()) {
                        outBlocks.add(NovelBlock.Heading(trimmed, level))
                    }
                }
                child is Element && child.normalName() in PARAGRAPH_TAGS -> {
                    flushInline()
                    processParagraphElement(child, outBlocks)
                }
                child is Element && child.normalName() in BLOCK_CONTAINER_TAGS -> {
                    flushInline()
                    walkContainer(child, outBlocks)
                }
                else -> {
                    appendNodeToContainerInline(child, inlineBuilder)
                }
            }
        }

        flushInline()
    }

    private fun processParagraphElement(element: Element, outBlocks: MutableList<NovelBlock>) {
        var pBuilder = AnnotatedString.Builder()

        fun flushP() {
            val annotated = pBuilder.toAnnotatedString()
            pBuilder = AnnotatedString.Builder()
            if (annotated.isEmpty()) return
            val slices = splitByBreak(annotated)
            for (slice in slices) {
                val trimmed = slice.trimWhitespace()
                if (trimmed.text.isNotEmpty()) {
                    if (isSceneBreak(trimmed.text)) {
                        outBlocks.add(NovelBlock.Divider)
                    } else {
                        outBlocks.add(NovelBlock.Paragraph(trimmed))
                    }
                }
            }
        }

        for (child in element.childNodes()) {
            if (child is Element && child.normalName() == "hr") {
                flushP()
                outBlocks.add(NovelBlock.Divider)
            } else if (child is Element && child.normalName() in PARAGRAPH_TAGS) {
                flushP()
                processParagraphElement(child, outBlocks)
            } else if (child is Element && child.normalName() in BLOCK_CONTAINER_TAGS) {
                flushP()
                walkContainer(child, outBlocks)
            } else {
                appendInlineNode(child, pBuilder, inContainer = false, isInsideHeading = false)
            }
        }

        flushP()
    }

    private fun appendNodeToContainerInline(node: Node, builder: AnnotatedString.Builder) {
        if (node is TextNode) {
            val text = node.wholeText
            val withBreaks = text.replace(DOUBLE_NEWLINE_REGEX, "\u2029")
            val parts = withBreaks.split("\u2029")
            val collapsed = parts.joinToString("\u2029") { part ->
                part.replace(WHITESPACE_COLLAPSE_REGEX, " ")
            }
            builder.append(collapsed)
        } else if (node is Element) {
            if (node.normalName() == "br") {
                builder.append("\u2029")
            } else {
                appendInlineNode(node, builder, inContainer = true, isInsideHeading = false)
            }
        }
    }

    private fun appendInlineNode(
        node: Node,
        builder: AnnotatedString.Builder,
        inContainer: Boolean,
        isInsideHeading: Boolean,
    ) {
        if (node is TextNode) {
            val text = node.wholeText
            val withBreaks = text.replace(DOUBLE_NEWLINE_REGEX, "\u2029")
            val parts = withBreaks.split("\u2029")
            val collapsed = parts.joinToString("\u2029") { part ->
                part.replace(WHITESPACE_COLLAPSE_REGEX, " ")
            }
            builder.append(collapsed)
            return
        }

        if (node !is Element) return

        val tagName = node.normalName()
        if (tagName == "br") {
            if (isInsideHeading) {
                builder.append("\n")
            } else if (inContainer) {
                builder.append("\u2029")
            } else {
                if (isMultiBr(node)) {
                    builder.append("\u2029")
                } else {
                    builder.append("\n")
                }
            }
            return
        }

        val pushBold = tagName == "b" || tagName == "strong"
        val pushItalic = tagName == "i" || tagName == "em"
        val pushStrike = tagName == "s" || tagName == "del" || tagName == "strike"
        val pushUnderline = tagName == "u"

        if (pushBold) builder.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        if (pushItalic) builder.pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
        if (pushStrike) builder.pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
        if (pushUnderline) builder.pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))

        for (child in node.childNodes()) {
            appendInlineNode(child, builder, inContainer, isInsideHeading)
        }

        if (pushUnderline) builder.pop()
        if (pushStrike) builder.pop()
        if (pushItalic) builder.pop()
        if (pushBold) builder.pop()
    }

    private fun isMultiBr(node: Node): Boolean {
        var prev = node.previousSibling()
        while (prev != null) {
            if (prev is Element && prev.normalName() == "br") return true
            if (prev is TextNode && prev.text().isNotBlank()) break
            if (prev is Element) break
            prev = prev.previousSibling()
        }
        var next = node.nextSibling()
        while (next != null) {
            if (next is Element && next.normalName() == "br") return true
            if (next is TextNode && next.text().isNotBlank()) break
            if (next is Element) break
            next = next.nextSibling()
        }
        return false
    }

    private fun splitByBreak(annotated: AnnotatedString): List<AnnotatedString> {
        if (annotated.isEmpty()) return emptyList()
        val slices = mutableListOf<AnnotatedString>()
        var start = 0
        val text = annotated.text
        for (i in text.indices) {
            if (text[i] == '\u2029') {
                if (i > start) {
                    slices.add(annotated.subSequence(start, i))
                }
                start = i + 1
            }
        }
        if (start < text.length) {
            slices.add(annotated.subSequence(start, text.length))
        }
        return slices
    }
}
