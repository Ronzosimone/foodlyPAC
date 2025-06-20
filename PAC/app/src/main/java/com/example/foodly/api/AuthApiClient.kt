package com.example.foodly.api

import android.content.Context
import android.util.Log
import com.example.foodly.api.request.LoginRequest
import com.example.foodly.api.response.LoginResponse
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
                    //preferencesManager.saveString("id", result.data.id)
                }

            }

            is com.example.foodly.api.Result.Error -> {
                // clear user token on error
                Log.e("AuthApiClient-login", "Error during login")
            }
        }
        return handleResultWithNullCheck(result, "LoginResponse data is missing")
    }
}

