package com.example.oraide.ui.search

import java.util.regex.Pattern

data class SearchOptions(
    val matchCase: Boolean = false,
    val wholeWord: Boolean = false,
    val useRegex: Boolean = false
)

object SearchEngine {
    
    fun compilePattern(query: String, options: SearchOptions): Regex {
        var patternStr = if (options.useRegex) query else Pattern.quote(query)
        if (options.wholeWord) {
            patternStr = "\\b\\b"
        }
        val regexOptions = if (options.matchCase) emptySet<RegexOption>() else setOf(RegexOption.IGNORE_CASE)
        return Regex(patternStr, regexOptions)
    }

    fun findMatchesInText(text: String, regex: Regex): Sequence<MatchResult> {
        return regex.findAll(text)
    }
}
