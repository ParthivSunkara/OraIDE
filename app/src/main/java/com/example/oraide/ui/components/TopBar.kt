package com.example.oraide.ui.components

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oraide.theme.oraideActivityBar
import com.example.oraide.theme.oraideText
import com.example.oraide.theme.oraideTextDark

@Composable
fun TopBar(
    projectName: String,
    fileName: String,
    onSaveClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Project and File Name
        Text(
            text = projectName,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        if (fileName.isNotEmpty()) {
            Text(
                text = " - ",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Text(
                text = fileName,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Actions
        IconButton(onClick = onSaveClick) {
            AppIconView(icon = AppIcon.SAVE, contentDescription = "Save", tint = MaterialTheme.colorScheme.onBackground)
        }
        IconButton(onClick = { /* Disabled Run */ }, enabled = false) {
            AppIconView(icon = AppIcon.RUN, contentDescription = "Run", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = onSearchClick) {
            AppIconView(icon = AppIcon.SEARCH, contentDescription = "Search", tint = MaterialTheme.colorScheme.onBackground)
        }
        IconButton(onClick = { /* Overflow */ }) {
            AppIconView(icon = AppIcon.MORE, contentDescription = "More", tint = MaterialTheme.colorScheme.onBackground)
        }
    }
}

