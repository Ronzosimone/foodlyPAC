package com.example.foodly.api

import android.content.Context
import android.util.Log
import com.example.foodly.api.response.ProfileResponse

object ProfileApiClient : BaseApiClient(NetworkSingleton.httpClient) {

    suspend fun getProfile(
        idUser: Int,
        context: Context
    ): com.example.foodly.api.Result<ProfileResponse> {
        val result = getRequest<ProfileResponse>(
            url = "GetProfile",
            queryParams = mapOf("id_user" to idUser.toString())
        )
        
        when (result) {
            is com.example.foodly.api.Result.Success -> {
                val profile = result.data
                if (profile != null) {
                    Log.d("ProfileApiClient", "Profile loaded for user: ${profile.name} ${profile.surname}")
                }
            }

            is com.example.foodly.api.Result.Error -> {
                Log.e("ProfileApiClient", "Error getting profile for user $idUser")
            }
        }
        
        return handleResultWithNullCheck(result, "ProfileResponse data is missing")
    }
}
