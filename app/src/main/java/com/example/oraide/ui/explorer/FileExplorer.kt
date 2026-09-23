package com.example.oraide.ui.explorer

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.isSecondaryPressed

@Composable
fun FileExplorer(
    fileTree: List<FileNode>,
    projectName: String,
    selectedNode: FileNode?,
    onNodeClicked: (FileNode) -> Unit,
    onNodeLongClicked: (FileNode) -> Unit,
    onCreateFile: (String, FileNode?) -> Unit,
    onCreateFolder: (String, FileNode?) -> Unit,
    onProjectMenuClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isNewFileDialogOpen by remember { mutableStateOf(false) }
    var newFileDialogTarget by remember { mutableStateOf<FileNode?>(null) }
    var isNewFolderDialogOpen by remember { mutableStateOf(false) }
    var newFolderDialogTarget by remember { mutableStateOf<FileNode?>(null) }

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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).clickable { onProjectMenuClicked() }) {
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
                IconButton(onClick = { newFileDialogTarget = null; isNewFileDialogOpen = true }, modifier = Modifier.size(24.dp)) {
                    AppIconView(icon = AppIcon.NEW_FILE, contentDescription = "New File", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { newFolderDialogTarget = null; isNewFolderDialogOpen = true }, modifier = Modifier.size(24.dp)) {
                    AppIconView(icon = AppIcon.NEW_FOLDER, contentDescription = "New Folder", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
            }
        }
        
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), thickness = 1.dp)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(fileTree, key = { it.path }) { node ->
                FileNodeItem(
                    node = node,
                    isActive = node == selectedNode,
                    level = node.level, 
                    onClick = { onNodeClicked(node) },
                    onLongClick = { onNodeLongClicked(node) },
                    onNewFile = { newFileDialogTarget = node; isNewFileDialogOpen = true },
                    onNewFolder = { newFolderDialogTarget = node; isNewFolderDialogOpen = true }
                )
            }
        }
    }

    if (isNewFileDialogOpen) {
        InputDialog(
            title = "New File",
            onConfirm = { 
                onCreateFile(it, newFileDialogTarget)
                isNewFileDialogOpen = false
            },
            onDismiss = { isNewFileDialogOpen = false }
        )
    }

    if (isNewFolderDialogOpen) {
        InputDialog(
            title = "New Folder",
            onConfirm = { 
                onCreateFolder(it, newFolderDialogTarget)
                isNewFolderDialogOpen = false
            },
            onDismiss = { isNewFolderDialogOpen = false }
        )
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileNodeItem(
    node: FileNode,
    isActive: Boolean,
    level: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onNewFile: () -> Unit = {},
    onNewFolder: () -> Unit = {}
) {
    val bgColor = if (isActive) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == androidx.compose.ui.input.pointer.PointerEventType.Press) {
                            if (event.buttons.isSecondaryPressed) {
                                onLongClick()
                                event.changes.forEach { it.consume() }
                            }
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onDoubleTap = { onClick() }, // Open file or expand
                    onLongPress = { onLongClick() }
                )
            }
            .padding(start = (16 + level * 16).dp, top = 4.dp, bottom = 4.dp, end = 16.dp)
    ) {
        val iconData = getFileIcon(node)
        if (node.isDirectory) {
            AppIconView(
                icon = if (node.isExpanded) AppIcon.CHEVRON_DOWN else AppIcon.CHEVRON_RIGHT,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            AppIconView(
                icon = iconData.icon,
                contentDescription = null,
                tint = iconData.color,
                modifier = Modifier.size(16.dp).padding(start = 2.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(16.dp))
            AppIconView(
                icon = iconData.icon,
                contentDescription = null,
                tint = iconData.color,
                modifier = Modifier.size(16.dp).padding(start = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = node.name,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        if (node.isDirectory) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNewFile, modifier = Modifier.size(24.dp)) {
                    AppIconView(icon = AppIcon.NEW_FILE, contentDescription = "New File", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onNewFolder, modifier = Modifier.size(24.dp)) {
                    AppIconView(icon = AppIcon.NEW_FOLDER, contentDescription = "New Folder", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

data class FileIconData(val icon: AppIcon, val color: androidx.compose.ui.graphics.Color)

@Composable
fun getFileIcon(node: FileNode): FileIconData {
    val defaultColor = MaterialTheme.colorScheme.onSurfaceVariant
    if (node.isDirectory) {
        return FileIconData(if (node.isExpanded) AppIcon.FOLDER_OPEN else AppIcon.EXPLORER, defaultColor)
    }
    
    return when (node.extension.lowercase()) {
        "c" -> FileIconData(AppIcon.FILE_C, androidx.compose.ui.graphics.Color(0xFF555555))
        "cpp", "cxx", "h", "hpp" -> FileIconData(AppIcon.FILE_CPP, androidx.compose.ui.graphics.Color(0xFF00599C))
        "kt", "kts" -> FileIconData(AppIcon.FILE_KOTLIN, androidx.compose.ui.graphics.Color(0xFF7F52FF))
        "java" -> FileIconData(AppIcon.FILE_JAVA, androidx.compose.ui.graphics.Color(0xFFB07219))
        "py" -> FileIconData(AppIcon.FILE_PYTHON, androidx.compose.ui.graphics.Color(0xFF3572A5))
        "json" -> FileIconData(AppIcon.FILE_JSON, androidx.compose.ui.graphics.Color(0xFFF2C94C))
        "xml" -> FileIconData(AppIcon.FILE_XML, androidx.compose.ui.graphics.Color(0xFF0060AC))
        "md" -> FileIconData(AppIcon.FILE_MD, androidx.compose.ui.graphics.Color(0xFF42A5F5))
        "png", "jpg", "jpeg", "gif", "webp" -> FileIconData(AppIcon.FILE_IMAGE, androidx.compose.ui.graphics.Color(0xFF4CAF50))
        else -> FileIconData(AppIcon.FILE, defaultColor)
    }
}
