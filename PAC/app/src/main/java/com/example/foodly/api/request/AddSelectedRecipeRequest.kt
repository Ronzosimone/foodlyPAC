package com.example.foodly.api.request

import kotlinx.serialization.Serializable

@Serializable
data class AddSelectedRecipeRequest(
    val id_recipe: Int,
    val id_user: Int
)
