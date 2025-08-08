package com.example.foodly.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val email: String,
    val id: Int,
    val name: String,
    val surname: String
)
