package com.example.foodly.api

import android.content.Context
import android.util.Log
import com.example.foodly.api.request.AddPantryRequest
import com.example.foodly.api.response.GetPantryResponse
import com.example.foodly.api.response.PantryResponse
import com.example.foodly.utils.UserPreferences
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.call.body
import io.ktor.http.URLProtocol
import io.ktor.http.path

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

        return try {
            // Chiamata diretta all'API senza usare BaseApiClient
            val response = NetworkSingleton.httpClient.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "ziiu2suca1.execute-api.eu-north-1.amazonaws.com/dev/api/v1"
                    path("GetPantry")
                    parameters.append("id_user", userId.toString())
                }
                headers {
                    val bearerToken = "qwerty"
                    append("Authorization", "Bearer $bearerToken")
                    Log.d("PantryApiClient", "GET User token: $bearerToken")
                }
            }

            when (response.status.value) {
                in 200..299 -> {
                    val responseBody = response.body<GetPantryResponse>()
                    Log.d("PantryApiClient", "Pantry retrieved successfully for user: $userId")
                    Result.Success(responseBody)
                }
                else -> {
                    val errorMessage = response.status.value.toString() + " " + response.status.description
                    Log.e("PantryApiClient", "Error retrieving pantry: $errorMessage")
                    Result.Error(errorMessage)
                }
            }
        } catch (e: Exception) {
            val errorMessage = "Errore di rete: ${e.message}"
            Log.e("PantryApiClient", errorMessage, e)
            Result.Error(errorMessage)
        }
    }
}

enum class UnitType {
    UNITS,
    GRAMS
}
