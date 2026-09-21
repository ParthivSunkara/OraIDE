package com.example.oraide.ui.editor

import androidx.compose.ui.text.input.TextFieldValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oraide.data.FileNode
import com.example.oraide.data.FileRepository
import com.example.oraide.data.SettingsManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditorViewModel(
    private val repository: FileRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {
    private val _tabs = MutableStateFlow<List<EditorTab>>(emptyList())
    val tabs: StateFlow<List<EditorTab>> = _tabs.asStateFlow()

    private val _activeIndex = MutableStateFlow<Int>(-1)
    val activeIndex: StateFlow<Int> = _activeIndex.asStateFlow()

    init {
        // Autosave loop
        viewModelScope.launch {
            while (true) {
                delay(2000)
                if (settingsManager.autoSave.value) {
                    val currentTabs = _tabs.value
                    currentTabs.forEachIndexed { index, tab ->
                        if (tab.isDirty) {
                            saveFile(index)
                        }
                    }
                }
            }
        }
    }

    fun openFile(file: DocumentFile) {
        viewModelScope.launch {
            val existingIndex = _tabs.value.indexOfFirst { it.file.uri == file.uri }
            if (existingIndex != -1) {
                _activeIndex.value = existingIndex
            } else {
                val content = repository.readFileContent(file)
                val newTab = EditorTab(file, TextFieldValue(content))
                val newList = _tabs.value + newTab
                _tabs.value = newList
                _activeIndex.value = newList.size - 1
            }
        }
    }
    
    fun openFileAndSelect(file: DocumentFile, start: Int, end: Int) {
        viewModelScope.launch {
            val existingIndex = _tabs.value.indexOfFirst { it.file.uri == file.uri }
            if (existingIndex != -1) {
                _activeIndex.value = existingIndex
                setSelection(existingIndex, start, end)
            } else {
                val content = repository.readFileContent(file)
                val newTab = EditorTab(file, TextFieldValue(content, androidx.compose.ui.text.TextRange(start, end)))
                val newList = _tabs.value + newTab
                _tabs.value = newList
                _activeIndex.value = newList.size - 1
            }
        }
    }
    
    fun clearAllTabs() {
        _tabs.value = emptyList()
        _activeIndex.value = -1
    }

    fun closeTabByFile(file: DocumentFile) {
        val existingIndex = _tabs.value.indexOfFirst { it.file.uri == file.uri }
        if (existingIndex != -1) {
            closeTab(existingIndex)
        }
    }

    fun updateTabFile(oldFile: DocumentFile, newFile: DocumentFile) {
        val list = _tabs.value.toMutableList()
        val index = list.indexOfFirst { it.file.uri == oldFile.uri }
        if (index != -1) {
            val tab = list[index]
            list[index] = tab.copy(file = newFile)
            _tabs.value = list
        }
    }

    fun closeTab(index: Int) {
        val newList = _tabs.value.toMutableList()
        if (index in newList.indices) {
            newList.removeAt(index)
            _tabs.value = newList
            if (newList.isEmpty()) {
                _activeIndex.value = -1
            } else if (_activeIndex.value >= newList.size) {
                _activeIndex.value = newList.size - 1
            }
        }
    }

    fun switchTab(index: Int) {
        if (index in _tabs.value.indices) {
            _activeIndex.value = index
        }
    }

    fun updateContent(index: Int, newContent: TextFieldValue) {
        val list = _tabs.value.toMutableList()
        if (index in list.indices) {
            val tab = list[index]
            // Only mark dirty if text actually changed, to avoid marking dirty on cursor moves
            val isDirty = tab.isDirty || tab.content.text != newContent.text
            
            // Push to undo stack if text changed significantly
            if (tab.content.text != newContent.text) {
                tab.undoManager.push(tab.content)
            }
            
            list[index] = tab.copy(content = newContent, isDirty = isDirty)
            _tabs.value = list
        }
    }

    fun updateContentByFile(file: DocumentFile, newText: String) {
        val list = _tabs.value.toMutableList()
        val index = list.indexOfFirst { it.file.uri == file.uri }
        if (index != -1) {
            val tab = list[index]
            val newContent = androidx.compose.ui.text.input.TextFieldValue(newText)
            list[index] = tab.copy(content = newContent, isDirty = true)
            _tabs.value = list
        }
    }

    fun undo(index: Int) {
        val list = _tabs.value.toMutableList()
        if (index in list.indices) {
            val tab = list[index]
            tab.undoManager.undo(tab.content)?.let { prev ->
                list[index] = tab.copy(content = prev, isDirty = true)
                _tabs.value = list
            }
        }
    }

    fun redo(index: Int) {
        val list = _tabs.value.toMutableList()
        if (index in list.indices) {
            val tab = list[index]
            tab.undoManager.redo()?.let { next ->
                list[index] = tab.copy(content = next, isDirty = true)
                _tabs.value = list
            }
        }
    }

    fun saveFile(index: Int) {
        val list = _tabs.value.toMutableList()
        if (index in list.indices) {
            val tab = list[index]
            viewModelScope.launch {
                repository.writeFileContent(tab.file, tab.content.text)
                val updatedList = _tabs.value.toMutableList()
                val currentIndex = updatedList.indexOfFirst { it.file.uri == tab.file.uri }
                if (currentIndex != -1) {
                    updatedList[currentIndex] = updatedList[currentIndex].copy(isDirty = false)
                    _tabs.value = updatedList
                }
            }
        }
    }

    fun setSelection(index: Int, start: Int, end: Int) {
        val list = _tabs.value.toMutableList()
        if (index in list.indices) {
            val tab = list[index]
            val newContent = tab.content.copy(selection = androidx.compose.ui.text.TextRange(start, end))
            list[index] = tab.copy(content = newContent)
            _tabs.value = list
        }
    }
}
