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
    modifier: Modifier = Modifier
) {
    val saveableStateHolder = rememberSaveableStateHolder()

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
                onTabClosed = onTabClosed
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
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().weight(1f))
            }
        }
    }
}
