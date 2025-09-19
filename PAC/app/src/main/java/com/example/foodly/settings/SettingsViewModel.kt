package com.example.foodly.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodly.api.ProfileApiClient
import com.example.foodly.api.Result
import com.example.foodly.api.response.ProfileResponse
import com.example.foodly.utils.UserPreferences
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

    // Diet switches (persisted in SharedPreferences as 0/1, default 0)
    private val _veganEnabled = MutableStateFlow(false)
    val veganEnabled: StateFlow<Boolean> = _veganEnabled.asStateFlow()

    private val _vegetarianEnabled = MutableStateFlow(false)
    val vegetarianEnabled: StateFlow<Boolean> = _vegetarianEnabled.asStateFlow()

    private val _glutenFreeEnabled = MutableStateFlow(false)
    val glutenFreeEnabled: StateFlow<Boolean> = _glutenFreeEnabled.asStateFlow()

    fun loadDietPrefs(context: Context) {
        val prefs = UserPreferences.getInstance(context)
        _veganEnabled.value = prefs.isVegan()
        _vegetarianEnabled.value = prefs.isVegetarian()
        _glutenFreeEnabled.value = prefs.isGlutenFree()
    }

    fun setVeganEnabled(context: Context, enabled: Boolean) {
        _veganEnabled.value = enabled
        UserPreferences.getInstance(context).setVegan(enabled)
    }

    fun setVegetarianEnabled(context: Context, enabled: Boolean) {
        _vegetarianEnabled.value = enabled
        UserPreferences.getInstance(context).setVegetarian(enabled)
    }

    fun setGlutenFreeEnabled(context: Context, enabled: Boolean) {
        _glutenFreeEnabled.value = enabled
        UserPreferences.getInstance(context).setGlutenFree(enabled)
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
    init { /* explicit load triggered from UI with context */ }
}
