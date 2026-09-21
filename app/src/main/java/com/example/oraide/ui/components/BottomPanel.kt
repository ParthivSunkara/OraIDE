package com.example.oraide.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun BottomPanel(
    activeProjectUri: String?,
    onClose: () -> Unit
) {
    var activeTab by remember { mutableStateOf("TERMINAL") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Resizable in the future, 250 is a good default
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Top Border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
        // Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomPanelTab(title = "TERMINAL", isSelected = activeTab == "TERMINAL", onClick = { activeTab = "TERMINAL" })
            BottomPanelTab(title = "PROBLEMS", isSelected = activeTab == "PROBLEMS", onClick = { activeTab = "PROBLEMS" })
            BottomPanelTab(title = "OUTPUT", isSelected = activeTab == "OUTPUT", onClick = { activeTab = "OUTPUT" })
            
            Spacer(modifier = Modifier.weight(1f))
            
            AppIconView(
                icon = AppIcon.CLOSE,
                contentDescription = "Close Panel",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onClose() }
            )
        }
        
        // Content Area
        Box(modifier = Modifier.fillMaxSize()) {
            when (activeTab) {
                "TERMINAL" -> TerminalContent(activeProjectUri)
                "PROBLEMS" -> PlaceholderContent("No problems detected.")
                "OUTPUT" -> PlaceholderContent("Output goes here.")
            }
        }
    }
}

@Composable
fun BottomPanelTab(title: String, isSelected: Boolean, onClick: () -> Unit) {
    val alpha = if (isSelected) 1.0f else 0.5f
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
        fontSize = 12.sp,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { onClick() }
    )
}

@Composable
fun PlaceholderContent(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
    }
}

@Composable
fun TerminalContent(activeProjectUri: String?) {
    // Basic terminal foundation
    var output by remember { mutableStateOf("user@tablet:~${activeProjectUri?.substringAfterLast(":") ?: "/Unknown"} $ ") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        Text(
            text = output,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}
