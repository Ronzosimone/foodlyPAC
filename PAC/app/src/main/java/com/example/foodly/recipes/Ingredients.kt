package com.example.foodly.recipes

data class Ingredient(
    val id: Int = 0, // Default ID, can be set later
    val name: String,
    val amount: Double = 0.0,
    val original: String? = null,
    val image: String? = null,
    val isChecked: Boolean = false,
    val grams: String? = "",
    val unit: String? = ""

)
