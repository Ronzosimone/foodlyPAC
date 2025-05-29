package com.example.foodly.backend

import kotlinx.serialization.Serializable

@Serializable
data class UserPantryItemRequest(
    // val ingredientId: String, // Using name as identifier for now, as per client-side PantryIngredientItem
    val name: String, // Name of the ingredient from PredefinedIngredients
    val quantity: Double,
    val unit: String
)

@Serializable
data class ResponseMessage(
    val message: String
)
