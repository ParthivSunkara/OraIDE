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

    
    fun setActiveSidebarItem(item: ActivityBarItem?) { _activeSidebarItem.value = item }

    fun setBottomPanelVisible(visible: Boolean) {
        _isBottomPanelVisible.value = visible
    }
}
