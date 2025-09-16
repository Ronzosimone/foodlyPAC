package com.example.foodly.api

import android.content.Context
import android.util.Log
import com.example.foodly.api.response.GreedyRecipesResponse
import com.example.foodly.utils.UserPreferences
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.call.body
import io.ktor.http.URLProtocol
import io.ktor.http.path

object RecipesApiClient : BaseApiClient(NetworkSingleton.httpClient) {
    suspend fun addSelectedRecipe(context: Context, recipeId: Int): Result<String> {
        val userId = com.example.foodly.utils.UserPreferences.getInstance(context).getUserId()
        if (userId == null) {
            Log.e("RecipesApiClient", "User ID not found in preferences")
            return Result.Error("User not logged in")
        }
        val request = com.example.foodly.api.request.AddSelectedRecipeRequest(
            id_recipe = recipeId,
            id_user = userId
        )
        val result = postRequest<com.example.foodly.api.request.AddSelectedRecipeRequest, com.example.foodly.api.response.BasicResponse<String>>("AddSelectedRecipe", request)
        return when (result) {
            is Result.Success -> {
                val message = result.data?.message ?: "Ricetta aggiunta con successo"
                Result.Success(message)
            }
            is Result.Error -> result
        }
    }
    suspend fun getGreedyRecipes(context: Context): Result<GreedyRecipesResponse> {
        // Recupera l'ID utente dalle SharedPreferences
        val userId = UserPreferences.getInstance(context).getUserId()

        if (userId == null) {
            Log.e("RecipesApiClient", "User ID not found in preferences")
            return Result.Error("User not logged in")
        }

        return try {
            // Chiamata diretta all'API senza usare BaseApiClient
            val response = NetworkSingleton.httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "ziiu2suca1.execute-api.eu-north-1.amazonaws.com/dev/api/v1"
                    path("GetGreedyRecipes")
                    parameters.append("id_user", userId.toString())
                }
                headers {
                    val bearerToken = "qwerty"
                    append("Authorization", "Bearer $bearerToken")
                    Log.d("RecipesApiClient", "GET User token: $bearerToken")
                }
            }

            when (response.status.value) {
                in 200..299 -> {
                    val responseBody = response.body<GreedyRecipesResponse>()
                    Log.d("RecipesApiClient", "Greedy recipes retrieved successfully for user: $userId")
                    Log.d("RecipesApiClient", "Found ${responseBody.data.ricette_selezionate.size} recommended recipes")
                    Result.Success(responseBody)
                }
                else -> {
                    val errorMessage = response.status.value.toString() + " " + response.status.description
                    Log.e("RecipesApiClient", "Error retrieving greedy recipes: $errorMessage")
                    Result.Error(errorMessage)
                }
            }
        } catch (e: Exception) {
            val errorMessage = "Errore di rete: ${e.message}"
            Log.e("RecipesApiClient", errorMessage, e)
            Result.Error(errorMessage)
        }
    }
}
