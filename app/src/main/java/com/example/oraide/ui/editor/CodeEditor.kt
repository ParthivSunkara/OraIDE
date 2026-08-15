package com.example.oraide.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oraide.data.SettingsManager

@Composable
fun CodeEditor(
    content: TextFieldValue,
    onContentChanged: (TextFieldValue) -> Unit,
    settingsManager: SettingsManager,
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchClosed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val highlighter = remember { RegexSyntaxHighlighter() }
    
    // Search state
    var localSearchQuery by remember { mutableStateOf(searchQuery) }
    var currentMatchIndex by remember { mutableStateOf(0) }
    
    val matches = remember(content.text, localSearchQuery) {
        if (localSearchQuery.isEmpty()) emptyList()
        else Regex.escape(localSearchQuery).toRegex(RegexOption.IGNORE_CASE).findAll(content.text).toList()
    }
    
    val visualTransformation = remember(highlighter, localSearchQuery, currentMatchIndex) { 
        SyntaxVisualTransformation(highlighter, localSearchQuery, currentMatchIndex) 
    }
    
    val textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onBackground
    )

    val autoIndent by settingsManager.autoIndent.collectAsState()
    val autoCloseBrackets by settingsManager.autoCloseBrackets.collectAsState()
    val autoCloseQuotes by settingsManager.autoCloseQuotes.collectAsState()
    val showLineNumbers by settingsManager.lineNumbers.collectAsState()
    val highlightCurrentLine by settingsManager.highlightCurrentLine.collectAsState()
    
    // Derive line numbers and current line
    val lines = content.text.split("\n")
    val lineNumbers = if (showLineNumbers) (1..lines.size).joinToString("\n") else ""
    
    val cursorPosition = content.selection.start
    val textBeforeCursor = content.text.substring(0, cursorPosition.coerceIn(0, content.text.length))
    val currentLineIndex = textBeforeCursor.count { it == '\n' }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (showLineNumbers) {
                Text(
                    text = lineNumbers,
                    style = textStyle.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End),
                    modifier = Modifier
                        .width(48.dp)
                        .padding(top = 8.dp, bottom = 8.dp, end = 16.dp)
                )
            }
            
            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                if (highlightCurrentLine) {
                    // Approximate current line highlight (simple version without text layout calculation)
                    // We can just highlight a band. However, without knowing exact line height it's hard.
                    // We'll skip exact drawing for now and keep it simple, or implement a rough one later.
                }

                BasicTextField(
                    value = content,
                    onValueChange = { newValue ->
                        var manipulatedValue = newValue
                        
                        // Handle auto-close brackets/quotes & auto-indent
                        if (newValue.text.length > content.text.length && newValue.selection.start == content.selection.start + 1) {
                            val typedChar = newValue.text[newValue.selection.start - 1]
                            
                            if (autoCloseBrackets && (typedChar == '{' || typedChar == '[' || typedChar == '(')) {
                                val closing = when (typedChar) {
                                    '{' -> "}"
                                    '[' -> "]"
                                    '(' -> ")"
                                    else -> ""
                                }
                                val newText = newValue.text.substring(0, newValue.selection.start) + closing + newValue.text.substring(newValue.selection.start)
                                manipulatedValue = TextFieldValue(newText, selection = newValue.selection)
                            } else if (autoCloseQuotes && (typedChar == '"' || typedChar == '\'')) {
                                val newText = newValue.text.substring(0, newValue.selection.start) + typedChar + newValue.text.substring(newValue.selection.start)
                                manipulatedValue = TextFieldValue(newText, selection = newValue.selection)
                            } else if (autoIndent && typedChar == '\n') {
                                // Find previous line indentation
                                val prevLineStart = content.text.lastIndexOf('\n', content.selection.start - 1).let { if (it == -1) 0 else it + 1 }
                                val prevLine = content.text.substring(prevLineStart, content.selection.start)
                                val indent = prevLine.takeWhile { it == ' ' || it == '\t' }
                                if (indent.isNotEmpty()) {
                                    val newText = newValue.text.substring(0, newValue.selection.start) + indent + newValue.text.substring(newValue.selection.start)
                                    manipulatedValue = TextFieldValue(newText, selection = TextRange(newValue.selection.start + indent.length))
                                }
                            }
                        }
                        
                        onContentChanged(manipulatedValue)
                    },
                    textStyle = textStyle,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                    visualTransformation = visualTransformation,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp)
                )
            }
        }
        
        if (isSearchActive) {
            SearchPanel(
                query = localSearchQuery,
                onQueryChange = { 
                    localSearchQuery = it
                    currentMatchIndex = 0
                },
                matchCount = matches.size,
                currentMatchIndex = currentMatchIndex,
                onNext = {
                    if (matches.isNotEmpty()) {
                        currentMatchIndex = (currentMatchIndex + 1) % matches.size
                        val match = matches[currentMatchIndex]
                        onContentChanged(content.copy(selection = TextRange(match.range.first, match.range.last + 1)))
                    }
                },
                onPrevious = {
                    if (matches.isNotEmpty()) {
                        currentMatchIndex = if (currentMatchIndex - 1 < 0) matches.size - 1 else currentMatchIndex - 1
                        val match = matches[currentMatchIndex]
                        onContentChanged(content.copy(selection = TextRange(match.range.first, match.range.last + 1)))
                    }
                },
                onClose = onSearchClosed,
                modifier = Modifier.align(androidx.compose.ui.Alignment.TopEnd).padding(16.dp)
            )
        }
    }
}
