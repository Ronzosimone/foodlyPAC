package com.example.foodly.login // Placing in .login package for now, can be moved to .registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Define possible UI states for the registration screen
sealed interface RegistrationUiState {
    object Idle : RegistrationUiState
    object Loading : RegistrationUiState
    object Success : RegistrationUiState
    data class Error(val message: String) : RegistrationUiState // Though not used in mock
}

class RegistrationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Idle)
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun register(email: String, pass: String, confirmPass: String) {
        viewModelScope.launch {
            if (pass != confirmPass) {
                _uiState.value = RegistrationUiState.Error("Passwords do not match.")
                delay(2000) // Keep error for a bit
                _uiState.value = RegistrationUiState.Idle
                return@launch
            }
            _uiState.value = RegistrationUiState.Loading
            // Simulate network delay
            delay(1500)
            // In a real app, you'd make a network call here.
            // For mock, we just assume success.
            println("Mock Registration: Email: $email, Password: $pass")
            _uiState.value = RegistrationUiState.Success
        }
    }

    // Function to reset the state
    fun resetState() {
        _uiState.value = RegistrationUiState.Idle
    }
}
