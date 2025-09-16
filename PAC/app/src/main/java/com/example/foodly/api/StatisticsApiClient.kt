package com.example.foodly.api

import android.content.Context
import android.util.Log
import com.example.foodly.api.request.StatisticsRequest
import com.example.foodly.api.response.StatisticsResponse
import com.example.foodly.utils.UserPreferences

object StatisticsApiClient : BaseApiClient(NetworkSingleton.httpClient) {

    suspend fun getRecipesStatistics(
        context: Context
    ): Result<StatisticsResponse> {
        // Recupera l'ID utente dalle SharedPreferences
        val userId = UserPreferences.getInstance(context).getUserId()
        
        if (userId == null) {
            Log.e("StatisticsApiClient", "User ID not found in preferences")
            return Result.Error("User not logged in")
        }

        // Usa GET request con query parameters invece di POST
        val queryParams = mapOf("id_user" to userId.toString())
        val result = getRequest<StatisticsResponse>("GetRecipes_Statistic", queryParams)
        
        when (result) {
            is Result.Success -> {
                Log.d("StatisticsApiClient", "Statistics retrieved successfully for user: $userId")
            }
            is Result.Error -> {
                Log.e("StatisticsApiClient", "Error retrieving statistics: ${result.message}")
            }
        }
        
        return handleResultWithNullCheck(result, "Statistics data is missing")
    }
}
