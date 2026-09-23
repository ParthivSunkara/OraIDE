package com.example.oraide.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.documentfile.provider.DocumentFile
import com.example.oraide.data.FileNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchSidePanel(
    searchViewModel: SearchViewModel,
    activeFile: DocumentFile?,
    activeDirectory: FileNode?,
    openTabsContent: Map<String, String>,
    onUpdateEditorTab: (DocumentFile, String) -> Unit,
    onResultClicked: (SearchResult) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchMode by remember { mutableStateOf("Find") } // "Find", "Find & Replace"
    var searchQuery by remember { mutableStateOf("") }
    var replaceQuery by remember { mutableStateOf("") }
    var searchScope by remember { mutableStateOf("Project") } // File, Folder, Project
    
    val searchResults by searchViewModel.searchResults.collectAsState()
    val isSearching by searchViewModel.isSearching.collectAsState()

    val performSearch = {
        searchViewModel.performSearch(searchQuery, searchScope, activeFile, activeDirectory, openTabsContent)
    }
    
    val performReplace = {
        searchViewModel.performReplace(searchQuery, replaceQuery, searchScope, activeFile, activeDirectory, openTabsContent, onUpdateEditorTab)
    }

    val onApply = {
        if (searchMode == "Find") {
            performSearch()
        } else {
            performReplace()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "SEARCH",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Mode Selection
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = searchMode == "Find",
                onClick = { searchMode = "Find" },
                label = { Text("Find", fontSize = 12.sp) }
            )
            FilterChip(
                selected = searchMode == "Find & Replace",
                onClick = { searchMode = "Find & Replace" },
                label = { Text("Find & Replace", fontSize = 12.sp) }
            )
        }
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search text", fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(imeAction = if (searchMode == "Find") ImeAction.Search else ImeAction.Next),
            keyboardActions = KeyboardActions(onSearch = { performSearch() })
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = replaceQuery,
            onValueChange = { replaceQuery = it },
            placeholder = { Text("Replace text", fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).alpha(if (searchMode == "Find") 0.5f else 1f),
            singleLine = true,
            enabled = searchMode == "Find & Replace",
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                disabledBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(onGo = { performReplace() })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Scope Selection
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = searchScope == "File",
                onClick = { searchScope = "File" },
                label = { Text("File", fontSize = 12.sp) }
            )
            FilterChip(
                selected = searchScope == "Folder",
                onClick = { searchScope = "Folder" },
                label = { Text("Folder", fontSize = 12.sp) }
            )
            FilterChip(
                selected = searchScope == "Project",
                onClick = { searchScope = "Project" },
                label = { Text("Project", fontSize = 12.sp) }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Search Options
        val options by searchViewModel.searchOptions.collectAsState()
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = options.matchCase,
                onClick = { searchViewModel.updateSearchOptions(options.copy(matchCase = !options.matchCase)) },
                label = { Text("Aa", fontSize = 12.sp) } // Case Sensitive
            )
            FilterChip(
                selected = options.wholeWord,
                onClick = { searchViewModel.updateSearchOptions(options.copy(wholeWord = !options.wholeWord)) },
                label = { Text("\\b", fontSize = 12.sp) } // Whole Word
            )
            FilterChip(
                selected = options.useRegex,
                onClick = { searchViewModel.updateSearchOptions(options.copy(useRegex = !options.useRegex)) },
                label = { Text(".*", fontSize = 12.sp) } // Regex
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onApply,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (searchMode == "Find") "Apply (Find)" else "Apply (Replace)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isSearching) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (searchResults.isNotEmpty()) {
            Text("${searchResults.size} matches in ${searchResults.map { it.file.uri }.distinct().size} files", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
            val resultsByFile = searchResults.groupBy { it.file }
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                resultsByFile.forEach { (file, results) ->
                    item {
                        Text(
                            text = file.name ?: "Unknown",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                    }
                    items(results) { result ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onResultClicked(result) }
                                .padding(vertical = 4.dp, horizontal = 12.dp)
                        ) {
                            Text(
                                text = "${result.lineNumber}: ",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            Text(
                                text = result.lineContent,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        } else if (searchQuery.isNotEmpty() && !isSearching) {
            Text(
                text = "No results found.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}
