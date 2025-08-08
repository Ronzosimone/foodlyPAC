package com.example.foodly.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodly.api.ProfileApiClient
import com.example.foodly.api.Result
import com.example.foodly.api.response.ProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Define possible UI states for the profile loading
sealed interface ProfileUiState {
    object Idle : ProfileUiState
    object Loading : ProfileUiState
    data class Success(val profile: ProfileResponse) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class SettingsViewModel : ViewModel() {

    // Profile state management
    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

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

    fun loadProfile(userId: Int, context: Context) {
        viewModelScope.launch {
            _profileUiState.value = ProfileUiState.Loading

            try {
                val result = ProfileApiClient.getProfile(userId, context)

                when (result) {
                    is Result.Success -> {
                        _profileUiState.value = ProfileUiState.Success(result.data)
                        // Update the user data with real profile data
                        _userName.value = "${result.data.name} ${result.data.surname}"
                        _userEmail.value = result.data.email
                    }
                    is Result.Error -> {
                        _profileUiState.value = ProfileUiState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState.Error("Network error occurred")
            }
        }
    }

    fun resetProfileState() {
        _profileUiState.value = ProfileUiState.Idle
    }

    // In a real app, these would have actual logic or be fetched from a repository
    init {
        // Load user settings if needed, for now, they are hardcoded.
    }
}
