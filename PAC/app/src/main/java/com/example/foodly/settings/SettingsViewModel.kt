package com.example.foodly.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {

    private val _userName = MutableStateFlow("Mario Rossi")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("mario.rossi@example.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userPhoneNumber = MutableStateFlow("+39 123 4567890")
    val userPhoneNumber: StateFlow<String> = _userPhoneNumber.asStateFlow()

    // Mock state for a notification switch
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    // In a real app, these would have actual logic or be fetched from a repository
    init {
        // Load user settings if needed, for now, they are hardcoded.
    }
}
