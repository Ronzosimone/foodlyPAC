package com.example.foodly.pantry

import kotlinx.serialization.Serializable

// Using Serializable in case we want to persist this or pass it around later.
@Serializable
data class PantryIngredientItem(
    val id: Int,
    val name: String,
    val defaultUnit: String // e.g., "g", "ml", "pcs"
)

object PredefinedIngredients {
    val items = listOf(
        PantryIngredientItem(1, "Carrots", "g"),
        PantryIngredientItem(2, "Potato", "g"),
        PantryIngredientItem(3, "Onion", "g"),
        PantryIngredientItem(4, "Garlic", "g"),
        PantryIngredientItem(5, "Tomato", "g"),
        PantryIngredientItem(6, "Parsley", "g"),
        PantryIngredientItem(7, "Bell pepper", "g"),
        PantryIngredientItem(8, "Peas", "g"),
        PantryIngredientItem(9, "Kale", "g"),
        PantryIngredientItem(10, "Celery", "g"),
        PantryIngredientItem(11, "Spinach", "g"),
        PantryIngredientItem(12, "Couliflower", "g"),
        PantryIngredientItem(13, "Cucumber", "g"),

        // Add more as desired
    )
}
