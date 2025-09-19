package com.example.foodly.api.request

import kotlinx.serialization.Serializable

@Serializable
data class AddPantryRequest(
    val id_user: Int,
    val id_ingredient: Int,
    val units: String? = null,
    val grams: String? = null
)

@Serializable
data class GetPantryRequest(
    val id_user: Int
)

@Serializable
data class DeletePantryRequest(
    val id_user: Int,
    val id_ingredient: Int? = null // Se null, elimina tutti gli ingredienti
)
