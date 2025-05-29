package com.example.foodly.data.network

import com.example.foodly.backend.Recipe // Assuming RecipeData.kt is accessible
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object RecipeService {
    private val client = HttpClient(Android) { // Using Android engine
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Good for robustness
            })
        }
        // Optionally, add default request parameters, logging, etc.
        // engine {
        //     connectTimeout = 10_000
        //     socketTimeout = 10_000
        // }
    }

    private const val BASE_URL = "http://127.0.0.1:8080" // Ktor server default

    suspend fun getRecipes(): List<Recipe> {
        return try {
            client.get("$BASE_URL/recipes").body<List<Recipe>>()
        } catch (e: Exception) {
            // Log error or rethrow as a custom domain exception
            println("Error fetching recipes: ${e.message}")
            emptyList() // Return empty list on error for now
        }
    }

    // Consider adding a function to close the client if needed, though for an object,
    // it might live for the app's lifecycle.
    // fun close() {
    //     client.close()
    // }
}
