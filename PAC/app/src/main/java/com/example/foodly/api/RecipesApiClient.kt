package com.example.foodly.api

import android.content.Context
import android.util.Log
import com.example.foodly.api.response.GreedyRecipesData
import com.example.foodly.utils.UserPreferences

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
                val message = result.data?.message ?: "Added recipe successfully"
                Result.Success(message)
            }
            is Result.Error -> result
        }
    }
    suspend fun getGreedyRecipes(context: Context): Result<GreedyRecipesData> {
        // Recupera l'ID utente dalle SharedPreferences
        val userId = UserPreferences.getInstance(context).getUserId()

        if (userId == null) {
            Log.e("RecipesApiClient", "User ID not found in preferences")
            return Result.Error("User not logged in")
        }
//vegan,vegetarian,glutenFree
    val prefs = UserPreferences.getInstance(context)
    val queryParams = mapOf(
        "id_user" to userId.toString(),
        "vegan" to prefs.getVeganInt().toString(),
        "vegetarian" to prefs.getVegetarianInt().toString(),
        "glutenFree" to prefs.getGlutenFreeInt().toString()
    )
    // BaseApiClient returns Result<GreedyRecipesData?>, convert to non-null Result
    val rawResult = getRequest<GreedyRecipesData>("GetGreedyRecipes", queryParams)
    return handleResultWithNullCheck(rawResult, "No data received for recipe recommendations")
    }
}
