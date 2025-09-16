package com.example.foodly.api.response

import kotlinx.serialization.Serializable

@Serializable
data class PantryResponse(
    val message: String,
    val status: String
)

@Serializable
data class GetPantryResponse(
    val data: List<PantryItem> = emptyList(),
    val message: String,
    val status: String
)

@Serializable
data class PantryItem(
    val cups: Double,
    val grams: Double,
    val id: Int,
    val id_ingredient: Int,
    val id_user: Int,
    val name_ingredient: String,
    val units: Int,
    val default_value: String
)
