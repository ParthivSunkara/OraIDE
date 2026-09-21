package com.example.oraide.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("oraide_settings", Context.MODE_PRIVATE)

    // Default Colors (Greyish-blue aesthetic)
    private val DEFAULT_BG_COLOR = "#0D1117"
    private val DEFAULT_SIDEBAR_COLOR = "#11161D"
    private val DEFAULT_ACTIVITY_BAR_COLOR = "#151B23"
    private val DEFAULT_ACCENT_COLOR = "#4FC3F7"

    // Theme Colors
    private val _backgroundColor = MutableStateFlow(prefs.getString("backgroundColor", DEFAULT_BG_COLOR) ?: DEFAULT_BG_COLOR)
    val backgroundColor: StateFlow<String> = _backgroundColor.asStateFlow()

    private val _sidebarColor = MutableStateFlow(prefs.getString("sidebarColor", DEFAULT_SIDEBAR_COLOR) ?: DEFAULT_SIDEBAR_COLOR)
    val sidebarColor: StateFlow<String> = _sidebarColor.asStateFlow()

    private val _activityBarColor = MutableStateFlow(prefs.getString("activityBarColor", DEFAULT_ACTIVITY_BAR_COLOR) ?: DEFAULT_ACTIVITY_BAR_COLOR)
    val activityBarColor: StateFlow<String> = _activityBarColor.asStateFlow()

    private val _accentColor = MutableStateFlow(prefs.getString("accentColor", DEFAULT_ACCENT_COLOR) ?: DEFAULT_ACCENT_COLOR)
    val accentColor: StateFlow<String> = _accentColor.asStateFlow()

    // Editor Settings
    private val _autoIndent = MutableStateFlow(prefs.getBoolean("autoIndent", true))
    val autoIndent: StateFlow<Boolean> = _autoIndent.asStateFlow()

    private val _autoSave = MutableStateFlow(prefs.getBoolean("autoSave", true))
    val autoSave: StateFlow<Boolean> = _autoSave.asStateFlow()

    private val _autoCloseBrackets = MutableStateFlow(prefs.getBoolean("autoCloseBrackets", true))
    val autoCloseBrackets: StateFlow<Boolean> = _autoCloseBrackets.asStateFlow()

    private val _autoCloseQuotes = MutableStateFlow(prefs.getBoolean("autoCloseQuotes", true))
    val autoCloseQuotes: StateFlow<Boolean> = _autoCloseQuotes.asStateFlow()

    private val _wordWrap = MutableStateFlow(prefs.getBoolean("wordWrap", false))
    val wordWrap: StateFlow<Boolean> = _wordWrap.asStateFlow()

    private val _lineNumbers = MutableStateFlow(prefs.getBoolean("lineNumbers", true))
    val lineNumbers: StateFlow<Boolean> = _lineNumbers.asStateFlow()

    private val _highlightCurrentLine = MutableStateFlow(prefs.getBoolean("highlightCurrentLine", true))
    val highlightCurrentLine: StateFlow<Boolean> = _highlightCurrentLine.asStateFlow()

    private val _uiScale = MutableStateFlow(prefs.getFloat("uiScale", 1.0f))
    val uiScale: StateFlow<Float> = _uiScale.asStateFlow()

    fun updateBackgroundColor(hex: String) {
        prefs.edit().putString("backgroundColor", hex).apply()
        _backgroundColor.value = hex
    }

    fun updateSidebarColor(hex: String) {
        prefs.edit().putString("sidebarColor", hex).apply()
        _sidebarColor.value = hex
    }

    fun updateActivityBarColor(hex: String) {
        prefs.edit().putString("activityBarColor", hex).apply()
        _activityBarColor.value = hex
    }

    fun updateAccentColor(hex: String) {
        prefs.edit().putString("accentColor", hex).apply()
        _accentColor.value = hex
    }

    fun updateAutoIndent(enabled: Boolean) {
        prefs.edit().putBoolean("autoIndent", enabled).apply()
        _autoIndent.value = enabled
    }

    fun updateAutoSave(enabled: Boolean) {
        prefs.edit().putBoolean("autoSave", enabled).apply()
        _autoSave.value = enabled
    }

    fun updateAutoCloseBrackets(enabled: Boolean) {
        prefs.edit().putBoolean("autoCloseBrackets", enabled).apply()
        _autoCloseBrackets.value = enabled
    }

    fun updateAutoCloseQuotes(enabled: Boolean) {
        prefs.edit().putBoolean("autoCloseQuotes", enabled).apply()
        _autoCloseQuotes.value = enabled
    }

    fun updateWordWrap(enabled: Boolean) {
        prefs.edit().putBoolean("wordWrap", enabled).apply()
        _wordWrap.value = enabled
    }

    fun updateLineNumbers(enabled: Boolean) {
        prefs.edit().putBoolean("lineNumbers", enabled).apply()
        _lineNumbers.value = enabled
    }

    fun updateHighlightCurrentLine(enabled: Boolean) {
        prefs.edit().putBoolean("highlightCurrentLine", enabled).apply()
        _highlightCurrentLine.value = enabled
    }

    fun updateUiScale(scale: Float) {
        val safeScale = scale.coerceIn(0.5f, 2.0f)
        prefs.edit().putFloat("uiScale", safeScale).apply()
        _uiScale.value = safeScale
    }

    fun resetToDefaults() {
        updateBackgroundColor(DEFAULT_BG_COLOR)
        updateSidebarColor(DEFAULT_SIDEBAR_COLOR)
        updateActivityBarColor(DEFAULT_ACTIVITY_BAR_COLOR)
        updateAccentColor(DEFAULT_ACCENT_COLOR)
        
        updateAutoIndent(true)
        updateAutoSave(true)
        updateAutoCloseBrackets(true)
        updateAutoCloseQuotes(true)
        updateWordWrap(false)
        updateLineNumbers(true)
        updateHighlightCurrentLine(true)
        updateUiScale(1.0f)
    }

    private val _projectUris = MutableStateFlow(prefs.getStringSet("projectUris", setOf()) ?: setOf())
    val projectUris: StateFlow<Set<String>> = _projectUris.asStateFlow()

    private val _activeProjectUri = MutableStateFlow(prefs.getString("activeProjectUri", prefs.getString("projectUri", null)))
    val activeProjectUri: StateFlow<String?> = _activeProjectUri.asStateFlow()

    init {
        // Migrate old projectUri if present
        val oldUri = prefs.getString("projectUri", null)
        if (oldUri != null) {
            if (!_projectUris.value.contains(oldUri)) {
                val newSet = _projectUris.value.toMutableSet().apply { add(oldUri) }
                prefs.edit().putStringSet("projectUris", newSet).apply()
                _projectUris.value = newSet
            }
            if (prefs.getString("activeProjectUri", null) == null) {
                setActiveProjectUri(oldUri)
            }
            prefs.edit().remove("projectUri").apply()
        }
    }

    fun addProjectUri(uri: String) {
        val newSet = _projectUris.value.toMutableSet().apply { add(uri) }
        prefs.edit().putStringSet("projectUris", newSet).apply()
        _projectUris.value = newSet
        setActiveProjectUri(uri)
    }

    fun removeProjectUri(uri: String) {
        val newSet = _projectUris.value.toMutableSet().apply { remove(uri) }
        prefs.edit().putStringSet("projectUris", newSet).apply()
        _projectUris.value = newSet
        if (_activeProjectUri.value == uri) {
            setActiveProjectUri(newSet.firstOrNull())
        }
    }

    fun setActiveProjectUri(uri: String?) {
        prefs.edit().putString("activeProjectUri", uri).apply()
        _activeProjectUri.value = uri
    }
}
