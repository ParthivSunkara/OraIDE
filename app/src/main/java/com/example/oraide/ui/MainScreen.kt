package com.example.oraide.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import com.example.oraide.data.SettingsManager
import com.example.oraide.ui.components.ActivityBar
import com.example.oraide.ui.components.BottomPanel
import com.example.oraide.ui.components.TopBar
import com.example.oraide.ui.editor.EditorArea
import com.example.oraide.ui.editor.EditorViewModel
import com.example.oraide.ui.explorer.FileExplorer
import com.example.oraide.ui.explorer.FileExplorerViewModel
import com.example.oraide.ui.settings.SettingsScreen

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    explorerViewModel: FileExplorerViewModel,
    editorViewModel: EditorViewModel,
    settingsManager: SettingsManager,
    onOpenProject: () -> Unit = {}
) {
    val activeSidebarItem by mainViewModel.activeSidebarItem.collectAsState()
    val isBottomPanelVisible by mainViewModel.isBottomPanelVisible.collectAsState()
    val isSearchActive by mainViewModel.isSearchActive.collectAsState()
    
    val fileTree by explorerViewModel.fileTree.collectAsState()
    
    val tabs by editorViewModel.tabs.collectAsState()
    val activeTabIndex by editorViewModel.activeIndex.collectAsState()
    val activeTab = tabs.getOrNull(activeTabIndex)

    Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        TopBar(
            projectName = "OraIDE",
            fileName = activeTab?.title ?: "",
            onSaveClick = {
                if (activeTabIndex != -1) {
                    editorViewModel.saveFile(activeTabIndex)
                }
            },
            onSearchClick = {
                mainViewModel.toggleSearch()
            }
        )
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Left Activity Bar
            ActivityBar(
                activeItem = activeSidebarItem,
                onItemSelected = { mainViewModel.toggleSidebarItem(it) }
            )

            // Sidebar (Explorer or Settings)
            if (activeSidebarItem == ActivityBarItem.EXPLORER) {
                if (explorerViewModel.projectName == "No Project") {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(250.dp)
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        androidx.compose.material3.Button(onClick = onOpenProject) {
                            androidx.compose.material3.Text("Open / Create Project")
                        }
                    }
                } else {
                    val activeDirectory by explorerViewModel.activeDirectory.collectAsState()
                    var nodeToRename by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.example.oraide.data.FileNode?>(null) }
                    var nodeToDelete by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.example.oraide.data.FileNode?>(null) }
                    var selectedNodeForMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.example.oraide.data.FileNode?>(null) }
                    
                    FileExplorer(
                        fileTree = fileTree,
                        projectName = explorerViewModel.projectName,
                        activeDirectory = activeDirectory,
                        onNodeClicked = { node ->
                            if (node.isDirectory) {
                                explorerViewModel.toggleFolder(node)
                            } else {
                                editorViewModel.openFile(node.file)
                            }
                        },
                        onNodeLongClicked = { node ->
                            selectedNodeForMenu = node
                        },
                        onCreateFile = { name -> 
                            explorerViewModel.createFile(name) { fileNode ->
                                if (fileNode != null && !fileNode.isDirectory) {
                                    editorViewModel.openFile(fileNode.file)
                                }
                            }
                        },
                        onCreateFolder = { name -> explorerViewModel.createFolder(name) }
                    )
                    
                    if (selectedNodeForMenu != null) {
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { selectedNodeForMenu = null },
                            title = { androidx.compose.material3.Text("Options for ${selectedNodeForMenu!!.name}") },
                            text = {
                                Column {
                                    androidx.compose.material3.TextButton(
                                        onClick = {
                                            nodeToRename = selectedNodeForMenu
                                            selectedNodeForMenu = null
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        androidx.compose.material3.Text("Rename")
                                    }
                                    androidx.compose.material3.TextButton(
                                        onClick = {
                                            nodeToDelete = selectedNodeForMenu
                                            selectedNodeForMenu = null
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        androidx.compose.material3.Text("Delete")
                                    }
                                }
                            },
                            confirmButton = {
                                androidx.compose.material3.TextButton(onClick = { selectedNodeForMenu = null }) {
                                    androidx.compose.material3.Text("Cancel")
                                }
                            }
                        )
                    }
                    
                    if (nodeToRename != null) {
                        com.example.oraide.ui.explorer.InputDialog(
                            title = "Rename",
                            initialText = nodeToRename!!.name,
                            onConfirm = { newName ->
                                val oldFile = nodeToRename!!.file
                                explorerViewModel.renameNode(nodeToRename!!, newName) { newNode ->
                                    if (newNode != null && !newNode.isDirectory) {
                                        editorViewModel.updateTabFile(oldFile, newNode.file)
                                    }
                                }
                                nodeToRename = null
                            },
                            onDismiss = { nodeToRename = null }
                        )
                    }
                    
                    if (nodeToDelete != null) {
                        com.example.oraide.ui.explorer.ConfirmDialog(
                            title = "Delete",
                            message = "Are you sure you want to delete ${nodeToDelete!!.name}? ${if (nodeToDelete!!.isDirectory) "All contents will be lost." else ""}",
                            onConfirm = {
                                val fileToClose = nodeToDelete!!.file
                                explorerViewModel.deleteNode(nodeToDelete!!) {
                                    editorViewModel.closeTabByFile(fileToClose)
                                }
                                nodeToDelete = null
                            },
                            onDismiss = { nodeToDelete = null }
                        )
                    }
                }
            } else if (activeSidebarItem == ActivityBarItem.SETTINGS) {
                SettingsScreen(
                    settingsManager = settingsManager,
                    modifier = Modifier.weight(1f) // Takes up remaining space when open, or give it fixed width?
                    // Wait, ActivityBar controls a sidebar, but Settings is usually a full tab or full editor area. 
                    // Let's just make Settings take up the whole remaining width for simplicity in V0.2, OR make it a sidebar and EditorArea next to it.
                    // SettingsScreen is currently written to take fillMaxSize. So we can put it in the "EditorArea" spot or "Sidebar" spot.
                    // Let's replace the whole remaining area if Settings is selected.
                )
            }
            
            // Editor & Bottom Panel Area (Only show if not settings, or keep settings in sidebar?)
            // OraIDE shows Settings as a tab. We can just show it instead of the EditorArea.
            if (activeSidebarItem != ActivityBarItem.SETTINGS) {
                Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                    EditorArea(
                        tabs = tabs,
                        activeIndex = activeTabIndex,
                        onTabSelected = { editorViewModel.switchTab(it) },
                        onTabClosed = { editorViewModel.closeTab(it) },
                        onContentChanged = { index, content -> editorViewModel.updateContent(index, content) },
                        isSearchActive = isSearchActive,
                        onSearchClosed = { mainViewModel.setSearchActive(false) },
                        settingsManager = settingsManager,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (isBottomPanelVisible) {
                        BottomPanel(
                            onClose = { mainViewModel.setBottomPanelVisible(false) }
                        )
                    }
                }
            }
        }
    }
}
