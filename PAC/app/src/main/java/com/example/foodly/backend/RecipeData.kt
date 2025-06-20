package com.example.foodly.backend

import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    val results: List<Recipe>
)

@Serializable
data class Recipe(
    val id: Int,
    val image: String,
    val imageType: String,
    val likes: Int,
    val missedIngredientCount: Int,
    val missedIngredients: List<Ingredient>,
    val title: String,
    val unusedIngredients: List<Ingredient>, // Assuming Ingredient structure is the same
    val usedIngredientCount: Int, // Changed to List<Ingredient> for consistency
    val usedIngredients: List<Ingredient>   // Assuming Ingredient structure is the same
)

@Serializable
data class Ingredient(
    // Made nullable as it might be missing
    val amount: Double,
    val extendedName: String?, // Made nullable
    val id: Int,
    val image: String?, // Made nullable
    val meta: List<String>?, // Made nullable
    val name: String,
    val original: String?, // Made nullable
    val originalName: String?, // Made nullable
    val unit: String? = "kg", // Made nullable
    val unitLong: String? = "kilogram", // Made nullable
    val unitShort: String? = "kg" // Made nullable
)

// Root structure for parsing the provided JSON string
@Serializable
data class RootRecipes(
    val recipes: RecipeResponseWrapper
)

@Serializable
data class RecipeResponseWrapper(
    val number: Int,
    val offset: Int,
    val results: List<Recipe>
)
