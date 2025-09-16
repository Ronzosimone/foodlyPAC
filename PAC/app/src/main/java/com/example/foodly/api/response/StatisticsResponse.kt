package com.example.foodly.api.response

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsResponse(
    val average: NutritionalAverage,
    val id_user: String,
    val recipes: Int,
)

@Serializable
data class NutritionalAverage(
    val calories: Double,
    val carbohydrates: Double,
    val fat: Double,
    val fiber: Double,
    val healty_score: Double,
    val protein: Double,
    val saturated_fat: Double
)
