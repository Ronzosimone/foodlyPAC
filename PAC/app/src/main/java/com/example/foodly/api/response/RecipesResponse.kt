package com.example.foodly.api.response

import kotlinx.serialization.Serializable

@Serializable
data class GreedyRecipesResponse(
    val status: String,
    val message: String,
    val data: GreedyRecipesData
)

@Serializable
data class GreedyRecipesData(
    val ingredienti_residui: Map<String, Double>,
    val ricette_selezionate: List<RecipeRecommendation>
)

@Serializable
data class RecipeRecommendation(
    val id: Int,
    val score: Double,
    val spoonacularSourceUrl: String,
    val title: String,
    val image: String? = null
)
