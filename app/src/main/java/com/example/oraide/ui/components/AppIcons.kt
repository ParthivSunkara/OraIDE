package com.example.oraide.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppIcon {
    EXPLORER,
    SEARCH,
    GIT,
    EXTENSIONS,
    SETTINGS,
    CLOSE,
    SAVE,
    RUN,
    MORE,
    FOLDER,
    FOLDER_OPEN,
    FILE,
    CHEVRON_DOWN,
    CHEVRON_RIGHT,
    NEW_FILE,
    NEW_FOLDER
}

@Composable
fun AppIconView(
    icon: AppIcon,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier
) {
    // For Version 0.1, we map to Material Symbols Rounded.
    // In future versions, this can be swapped out to load SVG paths directly
    // mimicking OraIDE icons without changing the surrounding UI.
    val imageVector: ImageVector = when (icon) {
        AppIcon.EXPLORER -> Icons.Rounded.Folder
        AppIcon.SEARCH -> Icons.Rounded.Search
        AppIcon.GIT -> Icons.Rounded.Create // Placeholder
        AppIcon.EXTENSIONS -> Icons.Rounded.Build // Placeholder
        AppIcon.SETTINGS -> Icons.Rounded.Settings
        AppIcon.CLOSE -> Icons.Rounded.Close
        AppIcon.SAVE -> Icons.Rounded.Save
        AppIcon.RUN -> Icons.Rounded.PlayArrow
        AppIcon.MORE -> Icons.Rounded.MoreVert
        AppIcon.FOLDER -> Icons.Rounded.KeyboardArrowRight
        AppIcon.FOLDER_OPEN -> Icons.Rounded.KeyboardArrowDown
        AppIcon.CHEVRON_DOWN -> Icons.Rounded.KeyboardArrowDown
        AppIcon.CHEVRON_RIGHT -> Icons.Rounded.KeyboardArrowRight
        AppIcon.FILE -> Icons.Rounded.Description
        AppIcon.NEW_FILE -> Icons.Rounded.Add
        AppIcon.NEW_FOLDER -> Icons.Rounded.Add
    }

    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
    )
}
