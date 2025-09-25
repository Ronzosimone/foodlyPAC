package com.example.foodly.api.response

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsResponse(
    val average: NutritionalAverage? = null,
    val id_user: String? = null,
    val recipes: Int? = null,
)

@Serializable
data class NutritionalAverage(
    val calories: Double? = null,
    val carbohydrates: Double? = null,
    val fat: Double? = null,
    val fiber: Double? = null,
    val healty_score: Double? = null,
    val protein: Double? = null,
    val saturated_fat: Double? = null
)
