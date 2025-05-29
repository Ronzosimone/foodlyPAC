package com.example.foodly.recipes


data class Ingredient(
    val name: String,
    val isChecked: Boolean = false,
    val grams: String? = "",
    val unit: String? = ""

)
