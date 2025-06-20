package com.example.foodly.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodly.api.AuthApiClient
import com.example.foodly.api.Result
import com.example.foodly.api.request.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Define possible UI states for the login screen
sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = AuthApiClient.login(LoginRequest(email, password), context)
            when (result) {
                is Result.Success -> _uiState.value = LoginUiState.Success
                is Result.Error -> _uiState.value = LoginUiState.Error(result.message)
            }
        }
    }

    // Function to reset the state if needed (e.g., after navigating away or showing an error)
    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
