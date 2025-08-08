package com.example.foodly.api.request

import kotlinx.serialization.Serializable

// Per il GetProfile non serve una request data class specifica
// perché usa solo query parameters, ma la creo per coerenza
data class GetProfileRequest(
    val idUser: Int
)
