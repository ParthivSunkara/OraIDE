package com.example.oraide.ui

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import com.example.oraide.data.SettingsManager
import com.example.oraide.ui.components.ActivityBar
import com.example.oraide.ui.components.BottomPanel
import com.example.oraide.ui.editor.EditorArea
import com.example.oraide.ui.editor.EditorViewModel
import com.example.oraide.ui.explorer.FileExplorer
import com.example.oraide.ui.explorer.FileExplorerViewModel
import com.example.oraide.ui.settings.SettingsScreen
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    explorerViewModel: FileExplorerViewModel,
    editorViewModel: EditorViewModel,
    searchViewModel: com.example.oraide.ui.search.SearchViewModel,
    settingsManager: SettingsManager,
    onOpenProject: () -> Unit = {}
) {
    val activeSidebarItem by mainViewModel.activeSidebarItem.collectAsState()
    val isBottomPanelVisible by mainViewModel.isBottomPanelVisible.collectAsState()
    val isSearchActive by mainViewModel.isSearchActive.collectAsState()
    
    val fileTree by explorerViewModel.fileTree.collectAsState()
    val projectName by explorerViewModel.projectName.collectAsState()
    
    val tabs by editorViewModel.tabs.collectAsState()
    val activeTabIndex by editorViewModel.activeIndex.collectAsState()
    val activeTab = tabs.getOrNull(activeTabIndex)

    val projectUris by settingsManager.projectUris.collectAsState()
    val activeProjectUri by settingsManager.activeProjectUri.collectAsState()
    var showProjectSwitcher by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showProjectMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showProjectRename by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showProjectDelete by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val saveAsLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        if (uri != null && activeTab != null) {
            val resolver = context.contentResolver
            try {
                resolver.openOutputStream(uri)?.use { output ->
                    output.write(activeTab.content.text.toByteArray())
                }
                val docFile = androidx.documentfile.provider.DocumentFile.fromSingleUri(context, uri)
                if (docFile != null) {
                    editorViewModel.updateTabFile(activeTab.file, docFile)
                    editorViewModel.saveFile(activeTabIndex) // Clear dirty flag
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding()
        .focusRequester(focusRequester)
        .focusable()
        .onPreviewKeyEvent { event ->
            com.example.oraide.ActionManager.handleKeyEvent(event) { command ->
                when (command) {
                    com.example.oraide.OraCommand.SAVE -> {
                        if (activeTabIndex != -1) editorViewModel.saveFile(activeTabIndex)
                        true
                    }
                    com.example.oraide.OraCommand.SAVE_AS -> {
                        if (activeTab != null) saveAsLauncher.launch(activeTab.file.name ?: "Untitled.txt")
                        true
                    }
                    com.example.oraide.OraCommand.CLOSE_TAB -> {
                        if (activeTabIndex != -1) editorViewModel.closeTab(activeTabIndex)
                        true
                    }
                    com.example.oraide.OraCommand.CLOSE_ALL_TABS -> {
                        editorViewModel.clearAllTabs()
                        true
                    }
                    com.example.oraide.OraCommand.FIND -> {
                        mainViewModel.setSearchActive(true)
                        true
                    }
                    com.example.oraide.OraCommand.FIND_REPLACE -> {
                        mainViewModel.setSearchActive(true)
                        // Trigger replace mode in UI via ViewModel (optional if supported)
                        true
                    }
                    com.example.oraide.OraCommand.NEXT_TAB -> {
                        if (tabs.isNotEmpty()) editorViewModel.switchTab((activeTabIndex + 1) % tabs.size)
                        true
                    }
                    com.example.oraide.OraCommand.PREV_TAB -> {
                        if (tabs.isNotEmpty()) editorViewModel.switchTab(if (activeTabIndex - 1 < 0) tabs.size - 1 else activeTabIndex - 1)
                        true
                    }
                    else -> false
                }
            }
        }
    ) {
        com.example.oraide.ui.components.GlobalAppBar(
            onSaveClick = {
                if (activeTabIndex != -1) {
                    editorViewModel.saveFile(activeTabIndex)
                }
            },
            onSaveAsClick = {
                if (activeTab != null) {
                    saveAsLauncher.launch(activeTab.file.name ?: "Untitled.txt")
                }
            },
            onSearchClick = {
                mainViewModel.toggleSearch()
            },
            onCloseAllTabsClick = { editorViewModel.clearAllTabs() },
            settingsManager = settingsManager
        )
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Left Activity Bar
            ActivityBar(
                activeItem = activeSidebarItem,
                onItemSelected = { item ->
                    if (item == ActivityBarItem.TERMINAL) {
                        mainViewModel.setBottomPanelVisible(!isBottomPanelVisible)
                    } else {
                        mainViewModel.toggleSidebarItem(item)
                    }
                }
            )

            // Sidebar (Explorer or Settings)
            if (activeSidebarItem == ActivityBarItem.EXPLORER) {
                if (projectName == "No Project") {
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
                        if (projectUris.isNotEmpty()) {
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                            androidx.compose.material3.Button(onClick = { showProjectSwitcher = true }) {
                                androidx.compose.material3.Text("Switch Project")
                            }
                        }
                    }
                } else {
                    val selectedNode by explorerViewModel.selectedNode.collectAsState()
                    var nodeToRename by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.example.oraide.data.FileNode?>(null) }
                    var nodeToDelete by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.example.oraide.data.FileNode?>(null) }
                    var selectedNodeForMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<com.example.oraide.data.FileNode?>(null) }
                    
                    FileExplorer(
                        fileTree = fileTree,
                        projectName = projectName,
                        selectedNode = selectedNode,
                        onNodeClicked = { node ->
                            explorerViewModel.selectNode(node)
                            if (node.isDirectory) {
                                explorerViewModel.toggleFolder(node)
                            } else {
                                editorViewModel.openFile(node.file)
                            }
                        },
                        onNodeLongClicked = { node ->
                            selectedNodeForMenu = node
                        },
                        onCreateFile = { name, targetParent -> 
                            explorerViewModel.createFile(name, targetParent) { fileNode ->
                                if (fileNode != null && !fileNode.isDirectory) {
                                    editorViewModel.openFile(fileNode.file)
                                }
                            }
                        },
                        onCreateFolder = { name, targetParent -> explorerViewModel.createFolder(name, targetParent) },
                        onProjectMenuClicked = { showProjectMenu = true }
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
            } else if (activeSidebarItem == ActivityBarItem.EXTENSIONS) {
                Box(modifier = Modifier.fillMaxHeight().width(250.dp)) {
                    com.example.oraide.ui.extensions.ExtensionsScreen()
                }
            } else if (activeSidebarItem == ActivityBarItem.SEARCH) {
                Box(modifier = Modifier.fillMaxHeight().width(250.dp)) {
                    com.example.oraide.ui.search.SearchSidePanel(
                        searchViewModel = searchViewModel,
                        activeFile = activeTab?.file,
                        activeDirectory = explorerViewModel.activeDirectory.collectAsState().value,
                        openTabsContent = tabs.associate { it.file.uri.toString() to it.content.text },
                        onUpdateEditorTab = { file, newText ->
                            editorViewModel.updateContentByFile(file, newText)
                        },
                        onResultClicked = { result ->
                            searchViewModel.setSelectedMatch(result)
                            editorViewModel.openFileAndSelect(result.file, result.matchStartIndex, result.matchEndIndex)
                        },
                        onClose = { mainViewModel.toggleSidebarItem(ActivityBarItem.EXPLORER) }
                    )
                }
            } else if (activeSidebarItem == ActivityBarItem.SETTINGS) {
                SettingsScreen(
                    settingsManager = settingsManager,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Editor & Bottom Panel Area
            if (activeSidebarItem != ActivityBarItem.SETTINGS) {
                Column(modifier = Modifier.weight(1f).fillMaxSize()) {
                    val globalSearchRegex by searchViewModel.searchRegex.collectAsState()
                    val globalSelectedMatch by searchViewModel.selectedMatch.collectAsState()
                    val globalMatchRange = globalSelectedMatch?.let {
                        if (it.file.uri == activeTab?.file?.uri) {
                            IntRange(it.matchStartIndex, it.matchEndIndex - 1)
                        } else null
                    }
                    
                    EditorArea(
                        tabs = tabs,
                        activeIndex = activeTabIndex,
                        onTabSelected = { editorViewModel.switchTab(it) },
                        onTabClosed = { editorViewModel.closeTab(it) },
                        onContentChanged = { index, content -> editorViewModel.updateContent(index, content) },
                        isSearchActive = isSearchActive,
                        onSearchClosed = { mainViewModel.setSearchActive(false) },
                        settingsManager = settingsManager,
                        searchRegex = globalSearchRegex,
                        globalSelectedMatchRange = globalMatchRange,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (isBottomPanelVisible) {
                        BottomPanel(
                            activeProjectUri = activeProjectUri,
                            onClose = { mainViewModel.setBottomPanelVisible(false) }
                        )
                    }
                }
            }
        }
        com.example.oraide.ui.components.StatusBar(projectName = projectName)
    }

    if (showProjectMenu) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showProjectMenu = false },
            title = { androidx.compose.material3.Text("Project Options") },
            text = {
                Column {
                    androidx.compose.material3.TextButton(
                        onClick = { showProjectSwitcher = true; showProjectMenu = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { androidx.compose.material3.Text("Switch Project") }
                    androidx.compose.material3.TextButton(
                        onClick = { onOpenProject(); showProjectMenu = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { androidx.compose.material3.Text("New Project") }
                    androidx.compose.material3.TextButton(
                        onClick = { showProjectRename = true; showProjectMenu = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { androidx.compose.material3.Text("Rename Project") }
                    androidx.compose.material3.TextButton(
                        onClick = { showProjectDelete = true; showProjectMenu = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { androidx.compose.material3.Text("Delete Project") }
                }
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { showProjectMenu = false }) {
                    androidx.compose.material3.Text("Cancel")
                }
            }
        )
    }

    if (showProjectRename) {
        com.example.oraide.ui.explorer.InputDialog(
            title = "Rename Project",
            initialText = projectName,
            onConfirm = { newName ->
                val uriStr = activeProjectUri
                if (uriStr != null) {
                    val oldPerms = context.contentResolver.persistedUriPermissions.map { it.uri.toString() }.toSet()
                    val docFile = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, android.net.Uri.parse(uriStr))
                    if (docFile != null && docFile.exists()) {
                        val success = docFile.renameTo(newName)
                        if (!success) {
                            android.widget.Toast.makeText(context, "Cannot rename this project directory due to Android storage restrictions.", android.widget.Toast.LENGTH_LONG).show()
                        } else {
                            editorViewModel.clearAllTabs()
                            explorerViewModel.loadProject()
                        }
                    }
                }
                showProjectRename = false
            },
            onDismiss = { showProjectRename = false }
        )
    }

    if (showProjectDelete) {
        com.example.oraide.ui.explorer.ConfirmDialog(
            title = "Delete Project",
            message = "Are you sure you want to delete this project? All files and folders inside will be permanently deleted.",
            onConfirm = {
                val uriStr = activeProjectUri
                if (uriStr != null) {
                    explorerViewModel.deleteProject(context, uriStr, settingsManager)
                    editorViewModel.clearAllTabs()
                }
                showProjectDelete = false
            },
            onDismiss = { showProjectDelete = false }
        )
    }

    if (showProjectSwitcher) {
        ProjectSwitcherDialog(
            projectUris = projectUris,
            currentProjectUri = activeProjectUri,
            onProjectSelected = { uriString ->
                if (activeProjectUri != uriString) {
                    editorViewModel.clearAllTabs()
                    settingsManager.setActiveProjectUri(uriString)
                    explorerViewModel.setWorkspaceRoot(uriString)
                }
                showProjectSwitcher = false
            },
            onProjectDeleted = { uriString ->
                if (activeProjectUri == uriString) {
                    editorViewModel.clearAllTabs()
                }
                explorerViewModel.deleteProject(context, uriString, settingsManager)
            },
            onDismiss = { showProjectSwitcher = false }
        )
    }
}

@Composable
fun ProjectSwitcherDialog(
    projectUris: Set<String>,
    currentProjectUri: String?,
    onProjectSelected: (String) -> Unit,
    onProjectDeleted: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var projectToDelete by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { androidx.compose.material3.Text("Projects") },
        text = {
            androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(projectUris.toList()) { uriString ->
                    val docFile = androidx.documentfile.provider.DocumentFile.fromTreeUri(context, android.net.Uri.parse(uriString))
                    val actualName = docFile?.name
                    val fallbackRaw = android.net.Uri.parse(uriString).lastPathSegment ?: uriString
                    val fallbackName = fallbackRaw.substringAfterLast(":")
                    val name = actualName ?: fallbackName
                    val isCurrent = uriString == currentProjectUri
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(if (isCurrent) androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f) else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { onProjectSelected(uriString) }
                            .padding(8.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = name,
                            modifier = Modifier.weight(1f),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                        )
                        androidx.compose.material3.IconButton(
                            onClick = { projectToDelete = uriString },
                            modifier = Modifier.size(24.dp)
                        ) {
                            com.example.oraide.ui.components.AppIconView(
                                icon = com.example.oraide.ui.components.AppIcon.CLOSE, 
                                contentDescription = "Delete Project", 
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            
            if (projectToDelete != null) {
                com.example.oraide.ui.explorer.ConfirmDialog(
                    title = "Delete Project",
                    message = "Are you sure you want to delete this project? All files and folders inside will be permanently deleted.",
                    onConfirm = {
                        onProjectDeleted(projectToDelete!!)
                        projectToDelete = null
                    },
                    onDismiss = { projectToDelete = null }
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text("Close")
            }
        }
    )
}
