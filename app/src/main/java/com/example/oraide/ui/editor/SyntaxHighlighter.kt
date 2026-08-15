package com.example.oraide.ui.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.Color
import com.example.oraide.theme.*

interface SyntaxHighlighter {
    fun highlight(text: String, searchQuery: String = "", currentMatchIndex: Int = -1): AnnotatedString
}

class RegexSyntaxHighlighter : SyntaxHighlighter {
    // Simple regex for basic syntax highlighting (Kotlin/Java/C like)
    private val keywords = listOf(
        "abstract", "annotation", "as", "break", "by", "catch", "class", "companion",
        "const", "constructor", "continue", "crossinline", "data", "delegate", "do",
        "dynamic", "else", "enum", "expect", "external", "false", "final", "finally",
        "for", "fun", "get", "if", "import", "in", "infix", "init", "inline", "inner",
        "interface", "internal", "is", "lateinit", "noinline", "null", "object", "open",
        "operator", "out", "override", "package", "private", "protected", "public",
        "reified", "return", "sealed", "set", "super", "suspend", "tailrec", "this",
        "throw", "true", "try", "typealias", "typeof", "val", "var", "vararg", "when", "while"
    )

    private val keywordRegex = "\\b(${keywords.joinToString("|")})\\b".toRegex()
    private val stringRegex = "\".*?\"".toRegex()
    private val numberRegex = "\\b\\d+\\b".toRegex()
    private val commentRegex = "//.*".toRegex()

    override fun highlight(text: String, searchQuery: String, currentMatchIndex: Int): AnnotatedString {
        return buildAnnotatedString {
            append(text)
            
            // Add default text color (it will be overridden by the text field's color, but just in case)
            addStyle(SpanStyle(color = oraideText), 0, text.length)

            // Numbers
            numberRegex.findAll(text).forEach { match ->
                addStyle(SpanStyle(color = oraideNumber), match.range.first, match.range.last + 1)
            }
            
            // Keywords
            keywordRegex.findAll(text).forEach { match ->
                addStyle(SpanStyle(color = oraideKeyword), match.range.first, match.range.last + 1)
            }

            // Strings
            stringRegex.findAll(text).forEach { match ->
                addStyle(SpanStyle(color = oraideString), match.range.first, match.range.last + 1)
            }
            
            // Comments (last so it overrides other things inside comments)
            commentRegex.findAll(text).forEach { match ->
                addStyle(SpanStyle(color = oraideComment), match.range.first, match.range.last + 1)
            }

            // Search Highlights
            if (searchQuery.isNotEmpty()) {
                val matches = Regex.escape(searchQuery).toRegex(RegexOption.IGNORE_CASE).findAll(text).toList()
                matches.forEachIndexed { index, match ->
                    val isCurrent = index == currentMatchIndex
                    val bgColor = if (isCurrent) Color(0xFFF6B93B) else Color(0x66F6B93B)
                    val fgColor = if (isCurrent) Color.Black else Color.Unspecified
                    addStyle(SpanStyle(background = bgColor, color = fgColor), match.range.first, match.range.last + 1)
                }
            }
        }
    }
}

class SyntaxVisualTransformation(
    private val highlighter: SyntaxHighlighter,
    private val searchQuery: String = "",
    private val currentMatchIndex: Int = -1
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            highlighter.highlight(text.text, searchQuery, currentMatchIndex),
            OffsetMapping.Identity
        )
    }
}
