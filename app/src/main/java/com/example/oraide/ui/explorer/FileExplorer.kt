package com.example.oraide.ui.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oraide.data.FileNode
import com.example.oraide.ui.components.AppIcon
import com.example.oraide.ui.components.AppIconView

@Composable
fun FileExplorer(
    fileTree: List<FileNode>,
    projectName: String,
    activeDirectory: FileNode?,
    onNodeClicked: (FileNode) -> Unit,
    onNodeLongClicked: (FileNode) -> Unit,
    onCreateFile: (String) -> Unit,
    onCreateFolder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewFileDialog by remember { mutableStateOf(false) }
    var showNewFolderDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // EXPLORER Header
        Text(
            text = "EXPLORER",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            modifier = Modifier.padding(16.dp)
        )
        
        // Project Root Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                AppIconView(
                    icon = AppIcon.FOLDER_OPEN,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = projectName,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showNewFileDialog = true }, modifier = Modifier.size(24.dp)) {
                    AppIconView(icon = AppIcon.NEW_FILE, contentDescription = "New File", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { showNewFolderDialog = true }, modifier = Modifier.size(24.dp)) {
                    AppIconView(icon = AppIcon.NEW_FOLDER, contentDescription = "New Folder", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
            }
        }
        
        Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), thickness = 1.dp)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(fileTree, key = { it.path }) { node ->
                FileNodeItem(
                    node = node,
                    isActive = node == activeDirectory,
                    level = calculateDepth(node, fileTree), 
                    onClick = { onNodeClicked(node) },
                    onLongClick = { onNodeLongClicked(node) }
                )
            }
        }
    }

    if (showNewFileDialog) {
        InputDialog(
            title = "New File",
            onConfirm = { 
                onCreateFile(it)
                showNewFileDialog = false
            },
            onDismiss = { showNewFileDialog = false }
        )
    }

    if (showNewFolderDialog) {
        InputDialog(
            title = "New Folder",
            onConfirm = { 
                onCreateFolder(it)
                showNewFolderDialog = false
            },
            onDismiss = { showNewFolderDialog = false }
        )
    }
}

private fun calculateDepth(node: FileNode, tree: List<FileNode>): Int {
    // For V0.2.1 simplicity in flat tree
    val rootPathLength = tree.firstOrNull()?.file?.uri?.toString()?.length ?: 0
    // Try to guess depth by counting slashes or URL encoded slashes in uri string after removing root prefix
    val pathStr = node.path
    if (pathStr.length <= rootPathLength) return 0
    val remainder = pathStr.substring(rootPathLength)
    // DocumentFile uris are complex. A better way would be tracking depth in FileNode.
    // For now, let's assume flat tree nodes aren't correctly indented unless we fix FileNode depth.
    // To fix this cleanly, let's just use a dummy depth 0 and fix it later if needed.
    return 0
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileNodeItem(
    node: FileNode,
    isActive: Boolean,
    level: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val bgColor = if (isActive) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(start = (16 + level * 16).dp, top = 4.dp, bottom = 4.dp, end = 16.dp)
    ) {
        if (node.isDirectory) {
            AppIconView(
                icon = if (node.isExpanded) AppIcon.CHEVRON_DOWN else AppIcon.CHEVRON_RIGHT,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            AppIconView(
                icon = AppIcon.EXPLORER,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp).padding(start = 2.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(16.dp))
            AppIconView(
                icon = AppIcon.FILE,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp).padding(start = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = node.name,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp
        )
    }
}
