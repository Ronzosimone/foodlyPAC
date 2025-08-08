package com.example.foodly.login // Placing in .login package for now, can be moved to .registration

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodly.api.AuthApiClient
import com.example.foodly.api.Result
import com.example.foodly.api.request.RegistrationRequest
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

    fun register(name: String, surname: String, email: String, password: String, confirmPassword: String, context: Context) {
        viewModelScope.launch {
            if (password != confirmPassword) {
                _uiState.value = RegistrationUiState.Error("Passwords do not match.")
                delay(2000) // Keep error for a bit
                _uiState.value = RegistrationUiState.Idle
                return@launch
            }

            if (name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank()) {
                _uiState.value = RegistrationUiState.Error("All fields are required.")
                delay(2000)
                _uiState.value = RegistrationUiState.Idle
                return@launch
            }

            _uiState.value = RegistrationUiState.Loading

            try {
                val request = RegistrationRequest(
                    name = name,
                    surname = surname,
                    email = email,
                    password = password
                )

                val result = AuthApiClient.register(request, context)

                when (result) {
                    is Result.Success -> {
                        _uiState.value = RegistrationUiState.Success
                    }
                    is Result.Error -> {
                        _uiState.value = RegistrationUiState.Error(result.message)
                        delay(3000)
                        _uiState.value = RegistrationUiState.Idle
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RegistrationUiState.Error("Network error occurred")
                delay(3000)
                _uiState.value = RegistrationUiState.Idle
            }
        }
    }

    // Function to reset the state
    fun resetState() {
        _uiState.value = RegistrationUiState.Idle
    }
}
