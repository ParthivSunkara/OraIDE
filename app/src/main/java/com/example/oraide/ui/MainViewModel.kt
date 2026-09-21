package com.example.oraide.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ActivityBarItem {
    EXPLORER, SEARCH, GIT, RUN, TERMINAL, EXTENSIONS, SETTINGS
}

class MainViewModel : ViewModel() {
    private val _activeSidebarItem = MutableStateFlow<ActivityBarItem?>(ActivityBarItem.EXPLORER)
    val activeSidebarItem: StateFlow<ActivityBarItem?> = _activeSidebarItem.asStateFlow()

    private val _isBottomPanelVisible = MutableStateFlow(false)
    val isBottomPanelVisible: StateFlow<Boolean> = _isBottomPanelVisible.asStateFlow()

    fun toggleSidebarItem(item: ActivityBarItem) {
        if (_activeSidebarItem.value == item) {
            _activeSidebarItem.value = null // Close if clicking same item
        } else {
            _activeSidebarItem.value = item
        }
    }

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    fun toggleSearch() {
        _isSearchActive.value = !_isSearchActive.value
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
    }

    fun setBottomPanelVisible(visible: Boolean) {
        _isBottomPanelVisible.value = visible
    }
}
