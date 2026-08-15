package com.example.oraide.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oraide.data.SettingsManager

@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    modifier: Modifier = Modifier
) {
    val autoIndent by settingsManager.autoIndent.collectAsState()
    val autoSave by settingsManager.autoSave.collectAsState()
    val autoCloseBrackets by settingsManager.autoCloseBrackets.collectAsState()
    val autoCloseQuotes by settingsManager.autoCloseQuotes.collectAsState()
    val wordWrap by settingsManager.wordWrap.collectAsState()
    val lineNumbers by settingsManager.lineNumbers.collectAsState()
    val highlightCurrentLine by settingsManager.highlightCurrentLine.collectAsState()

    val bgHex by settingsManager.backgroundColor.collectAsState()
    val sidebarHex by settingsManager.sidebarColor.collectAsState()
    val activityBarHex by settingsManager.activityBarColor.collectAsState()
    val accentHex by settingsManager.accentColor.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))

        // Theme Settings
        Text("Theme Colors (HEX)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        
        HexColorInput("Background Color", bgHex) { settingsManager.updateBackgroundColor(it) }
        HexColorInput("Sidebar Color", sidebarHex) { settingsManager.updateSidebarColor(it) }
        HexColorInput("Activity Bar Color", activityBarHex) { settingsManager.updateActivityBarColor(it) }
        HexColorInput("Accent Color", accentHex) { settingsManager.updateAccentColor(it) }
        
        Spacer(modifier = Modifier.height(24.dp))

        // Editor Behavior
        Text("Editor Behavior", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))

        SettingToggle("Auto-indent", autoIndent) { settingsManager.updateAutoIndent(it) }
        SettingToggle("Autosave", autoSave) { settingsManager.updateAutoSave(it) }
        SettingToggle("Auto-close Brackets", autoCloseBrackets) { settingsManager.updateAutoCloseBrackets(it) }
        SettingToggle("Auto-close Quotes", autoCloseQuotes) { settingsManager.updateAutoCloseQuotes(it) }
        SettingToggle("Word Wrap", wordWrap) { settingsManager.updateWordWrap(it) }
        SettingToggle("Line Numbers", lineNumbers) { settingsManager.updateLineNumbers(it) }
        SettingToggle("Highlight Current Line", highlightCurrentLine) { settingsManager.updateHighlightCurrentLine(it) }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { settingsManager.resetToDefaults() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Text("Reset to Defaults")
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Version 0.2.1",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun HexColorInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        OutlinedTextField(
            value = value,
            onValueChange = { 
                if (it.length <= 7) onValueChange(it) 
            },
            singleLine = true,
            modifier = Modifier.width(120.dp)
        )
    }
}

@Composable
fun SettingToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
