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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import com.example.oraide.ui.ActivityBarItem

@Composable
fun ActivityBar(
    activeItem: ActivityBarItem?,
    onItemSelected: (ActivityBarItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(72.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ActivityBarIcon(
            icon = AppIcon.EXPLORER,
            label = "Explorer",
            isSelected = activeItem == ActivityBarItem.EXPLORER,
            onClick = { onItemSelected(ActivityBarItem.EXPLORER) }
        )
        ActivityBarIcon(
            icon = AppIcon.SEARCH,
            label = "Search",
            isSelected = activeItem == ActivityBarItem.SEARCH,
            onClick = { onItemSelected(ActivityBarItem.SEARCH) }
        )
        ActivityBarIcon(
            icon = AppIcon.GIT,
            label = "Git",
            isSelected = activeItem == ActivityBarItem.GIT,
            onClick = { onItemSelected(ActivityBarItem.GIT) }
        )
        ActivityBarIcon(
            icon = AppIcon.RUN,
            label = "Run",
            isSelected = activeItem == ActivityBarItem.RUN,
            onClick = { onItemSelected(ActivityBarItem.RUN) }
        )
        ActivityBarIcon(
            icon = AppIcon.TERMINAL,
            label = "Terminal",
            isSelected = activeItem == ActivityBarItem.TERMINAL,
            onClick = { onItemSelected(ActivityBarItem.TERMINAL) }
        )
        ActivityBarIcon(
            icon = AppIcon.EXTENSIONS,
            label = "Extensions",
            isSelected = activeItem == ActivityBarItem.EXTENSIONS,
            onClick = { onItemSelected(ActivityBarItem.EXTENSIONS) }
        )

        Spacer(modifier = Modifier.weight(1f))

        ActivityBarIcon(
            icon = AppIcon.SETTINGS,
            label = "Settings",
            isSelected = activeItem == ActivityBarItem.SETTINGS,
            onClick = { onItemSelected(ActivityBarItem.SETTINGS) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ActivityBarIcon(
    icon: AppIcon,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppIconView(
                icon = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = contentColor,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
