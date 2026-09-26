package com.example.oraide.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oraide.ui.MainViewModel
import com.example.oraide.ui.editor.EditorViewModel
import com.example.oraide.data.SettingsManager

@Composable
fun GlobalAppBar(
    onSaveClick: () -> Unit,
    onSaveAsClick: () -> Unit,
    
    onCloseAllTabsClick: () -> Unit,
    settingsManager: SettingsManager,
    modifier: Modifier = Modifier
) {
    var moreMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LEFT: OraIDE Identity
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder logo dot
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onPrimary))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "OraIDE",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // RIGHT: Actions
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedToolbarIconButton(
                icon = AppIcon.RUN,
                contentDescription = "Run (Placeholder)",
                onClick = { /* Future */ }
            )
            OutlinedToolbarIconButton(
                icon = AppIcon.GIT,
                contentDescription = "Git (Placeholder)",
                onClick = { /* Future */ }
            )
            
            Box {
                OutlinedToolbarIconButton(
                    icon = AppIcon.MORE,
                    contentDescription = "More",
                    onClick = { moreMenuExpanded = true }
                )
                
                DropdownMenu(
                    expanded = moreMenuExpanded,
                    onDismissRequest = { moreMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Save") },
                        onClick = {
                            onSaveClick()
                            moreMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Save As...") },
                        onClick = {
                            onSaveAsClick()
                            moreMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Close All Tabs") },
                        onClick = {
                            onCloseAllTabsClick()
                            moreMenuExpanded = false
                        }
                    )
                    HorizontalDivider()
                    val uiScale by settingsManager.uiScale.collectAsState()
                    DropdownMenuItem(
                        text = { Text("UI Scale: ${(uiScale * 100).toInt()}%") },
                        onClick = {
                            // Cycle UI Scale or show a dialog. For now we can just increment and wrap.
                            val next = if (uiScale >= 2.0f) 0.5f else uiScale + 0.25f
                            settingsManager.updateUiScale(next)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Reset UI Scale") },
                        onClick = {
                            settingsManager.updateUiScale(1.0f)
                            moreMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OutlinedToolbarIconButton(
    icon: AppIcon,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(32.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .clip(CircleShape)
    ) {
        AppIconView(
            icon = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(16.dp)
        )
    }
}
