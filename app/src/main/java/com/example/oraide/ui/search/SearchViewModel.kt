package com.example.oraide.ui.search

import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oraide.data.FileNode
import com.example.oraide.data.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SearchResult(
    val file: DocumentFile,
    val lineContent: String,
    val lineNumber: Int,
    val matchStartIndex: Int,
    val matchEndIndex: Int
)

class SearchViewModel(private val repository: FileRepository) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedMatch = MutableStateFlow<SearchResult?>(null)
    val selectedMatch: StateFlow<SearchResult?> = _selectedMatch.asStateFlow()

    fun setSelectedMatch(match: SearchResult?) {
        _selectedMatch.value = match
    }

    fun performSearch(
        query: String, 
        scope: String, 
        activeFile: DocumentFile?, 
        activeDirectory: FileNode?,
        openTabsContent: Map<String, String> // Map of file URI string to editor content
    ) {
        _searchQuery.value = query
        _selectedMatch.value = null
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isSearching.value = true
            val results = mutableListOf<SearchResult>()
            val regex = Regex(Regex.escape(query), RegexOption.IGNORE_CASE)

            withContext(Dispatchers.IO) {
                when (scope) {
                    "File" -> {
                        activeFile?.let { file ->
                            searchInFile(file, regex, results, openTabsContent[file.uri.toString()])
                        }
                    }
                    "Folder" -> {
                        val folderToSearch = activeDirectory?.file ?: repository.getWorkspaceRoot()
                        folderToSearch?.let { folder ->
                            searchInFolder(folder, regex, results, openTabsContent)
                        }
                    }
                    "Project" -> {
                        repository.getWorkspaceRoot()?.let { root ->
                            searchInFolder(root, regex, results, openTabsContent)
                        }
                    }
                }
            }

            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun performReplace(
        searchQuery: String, 
        replaceQuery: String, 
        scope: String, 
        activeFile: DocumentFile?, 
        activeDirectory: FileNode?,
        openTabsContent: Map<String, String>,
        onUpdateEditorTab: (DocumentFile, String) -> Unit
    ) {
        if (searchQuery.isBlank()) return

        viewModelScope.launch {
            _isSearching.value = true
            val regex = Regex(Regex.escape(searchQuery), RegexOption.IGNORE_CASE)

            withContext(Dispatchers.IO) {
                when (scope) {
                    "File" -> {
                        activeFile?.let { file ->
                            replaceInFile(file, regex, replaceQuery, openTabsContent[file.uri.toString()], onUpdateEditorTab)
                        }
                    }
                    "Folder" -> {
                        val folderToSearch = activeDirectory?.file ?: repository.getWorkspaceRoot()
                        folderToSearch?.let { folder ->
                            replaceInFolder(folder, regex, replaceQuery, openTabsContent, onUpdateEditorTab)
                        }
                    }
                    "Project" -> {
                        repository.getWorkspaceRoot()?.let { root ->
                            replaceInFolder(root, regex, replaceQuery, openTabsContent, onUpdateEditorTab)
                        }
                    }
                }
            }
            
            // Re-run search after replace
            performSearch(searchQuery, scope, activeFile, activeDirectory, openTabsContent)
        }
    }

    private suspend fun searchInFolder(folder: DocumentFile, regex: Regex, results: MutableList<SearchResult>, openTabsContent: Map<String, String>) {
        val children = repository.getChildren(folder)
        for (node in children) {
            if (node.isDirectory) {
                searchInFolder(node.file, regex, results, openTabsContent)
            } else {
                searchInFile(node.file, regex, results, openTabsContent[node.file.uri.toString()])
            }
        }
    }

    private suspend fun searchInFile(file: DocumentFile, regex: Regex, results: MutableList<SearchResult>, memoryContent: String?) {
        try {
            val content = memoryContent ?: repository.readFileContent(file)
            val lines = content.lines()
            for ((index, line) in lines.withIndex()) {
                val matches = regex.findAll(line)
                for (match in matches) {
                    results.add(
                        SearchResult(
                            file = file,
                            lineContent = line.trim(),
                            lineNumber = index + 1,
                            matchStartIndex = match.range.first,
                            matchEndIndex = match.range.last
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Ignore files that can't be read (e.g. binaries)
        }
    }

    private suspend fun replaceInFolder(
        folder: DocumentFile, 
        regex: Regex, 
        replaceQuery: String, 
        openTabsContent: Map<String, String>,
        onUpdateEditorTab: (DocumentFile, String) -> Unit
    ) {
        val children = repository.getChildren(folder)
        for (node in children) {
            if (node.isDirectory) {
                replaceInFolder(node.file, regex, replaceQuery, openTabsContent, onUpdateEditorTab)
            } else {
                replaceInFile(node.file, regex, replaceQuery, openTabsContent[node.file.uri.toString()], onUpdateEditorTab)
            }
        }
    }

    private suspend fun replaceInFile(
        file: DocumentFile, 
        regex: Regex, 
        replaceQuery: String, 
        memoryContent: String?,
        onUpdateEditorTab: (DocumentFile, String) -> Unit
    ) {
        try {
            val content = memoryContent ?: repository.readFileContent(file)
            if (regex.containsMatchIn(content)) {
                val newContent = regex.replace(content, replaceQuery)
                if (memoryContent != null) {
                    // Update open tab
                    onUpdateEditorTab(file, newContent)
                } else {
                    // Write to file immediately since it's not open
                    repository.writeFileContent(file, newContent)
                }
            }
        } catch (e: Exception) {
            // Ignore files that can't be read
        }
    }
}
