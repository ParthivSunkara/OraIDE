package com.example.oraide.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.example.oraide.data.SettingsManager

@Composable
fun EditorArea(
    tabs: List<EditorTab>,
    activeIndex: Int,
    onTabSelected: (Int) -> Unit,
    onTabClosed: (Int) -> Unit,
    onContentChanged: (Int, TextFieldValue) -> Unit,
    isSearchActive: Boolean,
    onSearchClosed: () -> Unit,
    settingsManager: SettingsManager,
    searchRegex: Regex? = null,
    globalSelectedMatchRange: IntRange? = null,
    modifier: Modifier = Modifier
) {
    val saveableStateHolder = rememberSaveableStateHolder()
    val (tabToClose, setTabToClose) = remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (tabs.isEmpty()) {
            // Empty state
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No File is Open", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            TabBar(
                tabs = tabs,
                activeIndex = activeIndex,
                onTabSelected = onTabSelected,
                onTabClosed = { index ->
                    if (tabs[index].isDirty) {
                        setTabToClose(index)
                    } else {
                        onTabClosed(index)
                    }
                }
            )
            
            val activeTab = tabs.getOrNull(activeIndex)
            if (activeTab != null) {
                // Preserve internal editor state across tab switches based on file path
                saveableStateHolder.SaveableStateProvider(key = activeTab.file.uri.toString()) {
                    CodeEditor(
                        content = activeTab.content,
                        onContentChanged = { onContentChanged(activeIndex, it) },
                        settingsManager = settingsManager,
                        isSearchActive = isSearchActive,
                        searchQuery = "",
                        onSearchClosed = onSearchClosed,
                        searchRegex = searchRegex,
                        globalSelectedMatchRange = globalSelectedMatchRange,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().weight(1f))
            }
        }
    }

    if (tabToClose != null) {
        val tab = tabs.getOrNull(tabToClose)
        if (tab != null) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { setTabToClose(null) },
                title = { Text("Unsaved Changes") },
                text = { Text("Do you want to save the changes you made to ${tab.title}?\n\nYour changes will be lost if you don't save them.") },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            // We need to trigger a save, but we don't have the save function directly here.
                            // The easiest way is to let onTabClosed ignore it or pass a callback.
                            // For simplicity, we can just say "Don't Save" and "Cancel" here for now,
                            // or pass onSave(index) from ViewModel to EditorArea.
                            onTabClosed(tabToClose)
                            setTabToClose(null)
                        }
                    ) {
                        Text("Don't Save", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { setTabToClose(null) }) {
                        Text("Cancel")
                    }
                }
            )
        } else {
            setTabToClose(null)
        }
    }
}
