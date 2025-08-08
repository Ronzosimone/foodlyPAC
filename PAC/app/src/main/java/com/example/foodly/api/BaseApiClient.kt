package com.example.foodly.api

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body

import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import com.example.foodly.api.response.BasicResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json


abstract class BaseApiClient(protected val client: HttpClient) {
    protected suspend inline fun <reified Req, reified Res> postRequest(
        url: String,
        requestBody: Req?,
    ): Result<Res?> {
        return try {
            val response = client.post {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "ziiu2suca1.execute-api.eu-north-1.amazonaws.com/dev/api/v1"
                    path(url)
                }
                headers {
                    append("content-type", "application/json")
                    //append("Authorization", "Bearer ${getBearerToken()}")
                    val bearerToken = "qwerty"
                    append("Authorization", "Bearer $bearerToken")
                    Log.d("AuthApiClient", "User tokens:${bearerToken}")
                }
                contentType(ContentType.Application.Json)
                requestBody?.let { setBody(it) }
            }

            when (response.status.value) {
                in 200..299 -> {
                    val responseBody = response.body<BasicResponse<Res>>()
                    Result.Success(responseBody.data)
                }

                // TODO: FOR ERROR 600 AND 601 NEED TO CHECK THE ARRAY AND SPECIFY EACH FIELD THAT CONTAINS ERRORS
                in 600..601 -> {
                    val responseBody = response.body<BasicResponse<Array<String>>>()
                    val missingFields = responseBody.data?.joinToString() ?: ""
                    val errorMessage =  response.status.value.toString() + " " + response.status.description
                    Log.e("AuthApiClient", errorMessage)
                    Result.Error(errorMessage)
                }

                else -> {
                    val errorMessage =  response.status.value.toString() + " " + response.status.description
                    Log.e("AuthApiClient", errorMessage)
                    Result.Error(errorMessage)
                }
            }
        } catch (e: Exception) {
            val errorMessage = "Errore di rete"
            Log.e("AuthApiClient", errorMessage, e)
            Result.Error(errorMessage)
        }
    }

    protected suspend inline fun <reified Res> getRequest(
        url: String,
        queryParams: Map<String, String> = emptyMap()
    ): Result<Res?> {
        return try {
            val response = client.get {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "ziiu2suca1.execute-api.eu-north-1.amazonaws.com/dev/api/v1"
                    path(url)
                    queryParams.forEach { (key, value) ->
                        parameters.append(key, value)
                    }
                }
                headers {
                    //append("Authorization", "Bearer ${getBearerToken()}")
                    val bearerToken = "qwerty"
                    append("Authorization", "Bearer $bearerToken")
                    Log.d("ApiClient", "GET User token: $bearerToken")
                }
            }

            when (response.status.value) {
                in 200..299 -> {
                    val responseBody = response.body<BasicResponse<Res>>()
                    Result.Success(responseBody.data)
                }

                in 600..601 -> {
                    val responseBody = response.body<BasicResponse<Array<String>>>()
                    val missingFields = responseBody.data?.joinToString() ?: ""
                    val errorMessage = response.status.value.toString() + " " + response.status.description
                    Log.e("ApiClient", errorMessage)
                    Result.Error(errorMessage)
                }

                else -> {
                    val errorMessage = response.status.value.toString() + " " + response.status.description
                    Log.e("ApiClient", errorMessage)
                    Result.Error(errorMessage)
                }
            }
        } catch (e: Exception) {
            val errorMessage = "Errore di rete"
            Log.e("ApiClient", errorMessage, e)
            Result.Error(errorMessage)
        }
    }

    // Helper function to handle converting Result<Res?> to Result<Unit>
    protected fun <Res> handleResultForNoData(result: Result<Res?>): Result<Unit> {
        return when (result) {
            is Result.Success -> Result.Success(Unit) // Return success with no data
            is Result.Error -> result // Propagate the error
        }
    }

    // Utility function to handle result and propagate error if data is null or result is an error
    protected fun <T> handleResultWithNullCheck(
        result: Result<T?>,
        errorMessage: String
    ): Result<T> {
        return when (result) {
            is Result.Success -> {
                if (result.data != null) {
                    Result.Success(result.data)
                } else {
                    Result.Error(errorMessage)
                }
            }

            is Result.Error -> result // Propagate the error
        }
    }
}

object NetworkSingleton {
    @OptIn(ExperimentalSerializationApi::class)
    val httpClient: HttpClient by lazy {
        HttpClient() {
            install(HttpTimeout) {
                requestTimeoutMillis = 10000  // Set request timeout in milliseconds
                connectTimeoutMillis = 10000   // Set connect timeout
                socketTimeoutMillis = 15000   // Set socket timeout
            }
            install(ContentNegotiation) {
                json(Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                })

            }
          /*  install(Logging) {
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Log.d("Ktor", message)
                    }
                }
                level = LogLevel.ALL
                sanitizeHeader { false }
            }
            engine {
                requestTimeout = 10_000 // 10 seconds
            }*/

        }
    }
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

