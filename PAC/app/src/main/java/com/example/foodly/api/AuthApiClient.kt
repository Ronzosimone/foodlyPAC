package com.example.foodly.api

import android.content.Context
import android.util.Log
import com.example.foodly.api.request.LoginRequest
import com.example.foodly.api.request.RegistrationRequest
import com.example.foodly.api.response.LoginResponse
import com.example.foodly.api.response.RegistrationResponse
import com.example.foodly.utils.UserPreferences
import kotlin.Result


object AuthApiClient : BaseApiClient(NetworkSingleton.httpClient) {

    /* lateinit var preferencesManager: EncryptedPreferencesManager

     fun initialize(context: Context) {
         preferencesManager = EncryptedPreferencesManager(context)
     }
 */

    suspend fun login(
        request: LoginRequest,
        context: Context,
        sendFirebaseToken: Boolean = true
    ): com.example.foodly.api.Result<LoginResponse> { //Login or Login OTP
        val result = postRequest<LoginRequest, LoginResponse>("Login", request)
        when (result) {
            is com.example.foodly.api.Result.Success -> {
                // save user token
                val id = result.data?.id
                if (id != null) {
                    Log.d("AuthApiClient-login", "User token: $id")
                    // Salva l'ID utente nelle SharedPreferences
                    UserPreferences.getInstance(context).saveUserId(id)
                }

            }

            is com.example.foodly.api.Result.Error -> {
                // clear user token on error
                Log.e("AuthApiClient-login", "Error during login")
                // Pulisci le preferences in caso di errore
                UserPreferences.getInstance(context).logout()
            }
        }
        return handleResultWithNullCheck(result, "LoginResponse data is missing")
    }

    suspend fun register(
        request: RegistrationRequest,
        context: Context
    ): com.example.foodly.api.Result<RegistrationResponse> {
        val result = postRequest<RegistrationRequest, RegistrationResponse>("Registration", request)
        when (result) {
            is com.example.foodly.api.Result.Success -> {
                val id = result.data?.id
                if (id != null) {
                    Log.d("AuthApiClient-register", "User registered with id: $id")
                    // Salva l'ID utente nelle SharedPreferences dopo registrazione
                    UserPreferences.getInstance(context).saveUserId(id)
                }
            }

            is com.example.foodly.api.Result.Error -> {
                Log.e("AuthApiClient-register", "Error during registration")
                // Pulisci le preferences in caso di errore
                UserPreferences.getInstance(context).logout()
            }
        }
        return handleResultWithNullCheck(result, "RegistrationResponse data is missing")
    }
}

