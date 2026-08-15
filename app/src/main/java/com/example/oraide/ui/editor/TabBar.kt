package com.example.oraide.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oraide.ui.components.AppIcon
import com.example.oraide.ui.components.AppIconView

@Composable
fun TabBar(
    tabs: List<EditorTab>,
    activeIndex: Int,
    onTabSelected: (Int) -> Unit,
    onTabClosed: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        itemsIndexed(tabs) { index, tab ->
            val isActive = index == activeIndex
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .background(if (isActive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = tab.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                AppIconView(
                    icon = AppIcon.CLOSE,
                    contentDescription = "Close Tab",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onTabClosed(index) }
                )
            }
            if (!isActive) {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color.Black.copy(alpha = 0.2f)))
            }
        }
    }
}
