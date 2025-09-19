package com.example.foodly.api

import android.content.Context
import android.util.Log
import com.example.foodly.api.request.AddPantryRequest
import com.example.foodly.api.request.DeletePantryRequest
import com.example.foodly.api.response.GetPantryResponse
import com.example.foodly.api.response.PantryItem
import com.example.foodly.api.response.PantryResponse
import com.example.foodly.utils.UserPreferences
// No direct Ktor calls here; we use BaseApiClient helpers

object PantryApiClient : BaseApiClient(NetworkSingleton.httpClient) {

    suspend fun addPantryItem(
        context: Context,
        ingredientId: Int,
        quantity: String,
        unitType: UnitType
    ): Result<PantryResponse> {
        // Recupera l'ID utente dalle SharedPreferences
        val userId = UserPreferences.getInstance(context).getUserId()

        if (userId == null) {
            Log.e("PantryApiClient", "User ID not found in preferences")
            return Result.Error("User not logged in")
        }

        // Crea la request in base al tipo di unità
        val request = when (unitType) {
            UnitType.UNITS -> AddPantryRequest(
                id_user = userId,
                id_ingredient = ingredientId,
                units = quantity,
                grams = null
            )
            UnitType.GRAMS -> AddPantryRequest(
                id_user = userId,
                id_ingredient = ingredientId,
                units = null,
                grams = quantity
            )
        }

        val result = postRequest<AddPantryRequest, PantryResponse>("AddPantry", request)

        when (result) {
            is Result.Success -> {
                Log.d("PantryApiClient", "Pantry item added successfully for user: $userId")
                return Result.Success(PantryResponse(
                    message = "Ingrediente aggiunto con successo",
                    status = "OK"
                ))
            }
            is Result.Error -> {
                Log.e("PantryApiClient", "Error adding pantry item: ${result.message}")
                return result
            }
        }
    }

    suspend fun getPantry(
        context: Context
    ): Result<GetPantryResponse> {
        // Recupera l'ID utente dalle SharedPreferences
        val userId = UserPreferences.getInstance(context).getUserId()

        if (userId == null) {
            Log.e("PantryApiClient", "User ID not found in preferences")
            return Result.Error("User not logged in")
        }

        val query = mapOf("id_user" to userId.toString())
        val result = getRequest<List<PantryItem>>("GetPantry", query)
        return when (result) {
            is Result.Success -> {
                val items = result.data ?: emptyList()
                Result.Success(
                    GetPantryResponse(
                        data = items,
                        message = "",
                        status = "OK"
                    )
                )
            }
            is Result.Error -> result
        }
    }

    suspend fun deletePantryItem(
        context: Context,
        ingredientId: Int? = null // Se null, elimina tutti gli ingredienti
    ): Result<PantryResponse> {
        // Recupera l'ID utente dalle SharedPreferences
        val userId = UserPreferences.getInstance(context).getUserId()

        if (userId == null) {
            Log.e("PantryApiClient", "User ID not found in preferences")
            return Result.Error("User not logged in")
        }
        val request = DeletePantryRequest(
            id_user = userId,
            id_ingredient = ingredientId
        )

        val result = postRequest<DeletePantryRequest, PantryResponse>("DeletePantry", request)
        return when (result) {
            is Result.Success -> {
                val message = if (ingredientId == null) {
                    "Tutti gli ingredienti sono stati eliminati"
                } else {
                    "Ingrediente eliminato con successo"
                }
                Log.d("PantryApiClient", "Pantry item(s) deleted successfully for user: $userId")
                Result.Success(
                    PantryResponse(
                        message = message,
                        status = "OK"
                    )
                )
            }
            is Result.Error -> {
                Log.e("PantryApiClient", "Error deleting pantry item: ${result.message}")
                result
            }
        }
    }
}

enum class UnitType {
    UNITS,
    GRAMS
}
