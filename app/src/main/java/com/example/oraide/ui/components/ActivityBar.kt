package com.example.oraide.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.oraide.ui.ActivityBarItem

@Composable
fun ActivityBar(
    activeItem: ActivityBarItem?,
    onItemSelected: (ActivityBarItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ActivityBarIcon(
            icon = AppIcon.EXPLORER,
            isSelected = activeItem == ActivityBarItem.EXPLORER,
            onClick = { onItemSelected(ActivityBarItem.EXPLORER) }
        )
        ActivityBarIcon(
            icon = AppIcon.SEARCH,
            isSelected = activeItem == ActivityBarItem.SEARCH,
            onClick = { onItemSelected(ActivityBarItem.SEARCH) }
        )
        ActivityBarIcon(
            icon = AppIcon.GIT, // Placeholder for Git
            isSelected = activeItem == ActivityBarItem.GIT,
            onClick = { onItemSelected(ActivityBarItem.GIT) }
        )
        ActivityBarIcon(
            icon = AppIcon.EXTENSIONS, // Placeholder for Extensions
            isSelected = activeItem == ActivityBarItem.EXTENSIONS,
            onClick = { onItemSelected(ActivityBarItem.EXTENSIONS) }
        )

        Spacer(modifier = Modifier.weight(1f))

        ActivityBarIcon(
            icon = AppIcon.SETTINGS,
            isSelected = activeItem == ActivityBarItem.SETTINGS,
            onClick = { onItemSelected(ActivityBarItem.SETTINGS) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ActivityBarIcon(
    icon: AppIcon,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.CenterStart)
            )
        }
        AppIconView(
            icon = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}
