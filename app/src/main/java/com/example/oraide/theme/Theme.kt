package com.example.oraide.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.oraide.data.SettingsManager

fun parseColor(hex: String, defaultColor: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        defaultColor
    }
}

@Composable
fun OraIDETheme(
    settingsManager: SettingsManager?,
    content: @Composable () -> Unit,
) {
    val bgHex by settingsManager?.backgroundColor?.collectAsState() ?: androidx.compose.runtime.mutableStateOf("#1E2227")
    val sidebarHex by settingsManager?.sidebarColor?.collectAsState() ?: androidx.compose.runtime.mutableStateOf("#21252B")
    val activityBarHex by settingsManager?.activityBarColor?.collectAsState() ?: androidx.compose.runtime.mutableStateOf("#282C34")
    val accentHex by settingsManager?.accentColor?.collectAsState() ?: androidx.compose.runtime.mutableStateOf("#61AFEF")

    // The code editor background remains opaque for readability
    val background = parseColor(bgHex, Color(0xFF1E2227))
    // Add transparency to panels
    val surface = parseColor(sidebarHex, Color(0xFF21252B)).copy(alpha = 0.85f)
    val surfaceVariant = parseColor(activityBarHex, Color(0xFF282C34)).copy(alpha = 0.85f)
    val accent = parseColor(accentHex, Color(0xFF61AFEF))

    val colorScheme = darkColorScheme(
        primary = accent,
        secondary = accent,
        tertiary = accent,
        background = background,
        surface = surface,
        surfaceVariant = surfaceVariant,
        onPrimary = background,
        onSecondary = background,
        onTertiary = background,
        onBackground = oraideText,
        onSurface = oraideText,
        onSurfaceVariant = oraideTextDark,
        outline = oraideDivider
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
