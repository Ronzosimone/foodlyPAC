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
        PantryIngredientItem(1, "Flour", "g"),
        PantryIngredientItem(2, "Sugar", "g"),
        PantryIngredientItem(3, "Eggs", "pcs"),
        PantryIngredientItem(4, "Milk", "ml"),
        PantryIngredientItem(5, "Olive Oil", "ml"),
        PantryIngredientItem(6, "Onion", "pcs"),
        PantryIngredientItem(7, "Garlic", "cloves"),
        PantryIngredientItem(8, "Tomatoes", "pcs"),
        PantryIngredientItem(9, "Chicken Breast", "g"),
        PantryIngredientItem(10, "Rice", "g"),
        PantryIngredientItem(11, "Pasta", "g"),
        PantryIngredientItem(12, "Salt", "g"),
        PantryIngredientItem(13, "Pepper", "g"),
        PantryIngredientItem(14, "Butter", "g"),
        PantryIngredientItem(15, "Potatoes", "pcs"),
        PantryIngredientItem(16, "Carrots", "pcs"),
        PantryIngredientItem(17, "Cheese", "g"),
        PantryIngredientItem(18, "Bread", "slices"),
        PantryIngredientItem(19, "Lettuce", "pcs"),
        PantryIngredientItem(20, "Beans", "cans"),
        PantryIngredientItem(21, "Apples", "pcs"),
        PantryIngredientItem(22, "Bananas", "pcs"),
        PantryIngredientItem(23, "Orange Juice", "ml"),
        PantryIngredientItem(24, "Ground Beef", "g"),
        PantryIngredientItem(25, "Yogurt", "g")
        // Add more as desired
    )
}
