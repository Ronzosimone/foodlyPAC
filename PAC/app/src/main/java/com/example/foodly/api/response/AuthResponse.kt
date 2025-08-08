package com.example.foodly.api.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val email: String,
    val id: Int,
    val name: String,
    val password: String,
    val surname: String,
)

@Serializable
data class RegistrationResponse(
    val email: String,
    val id: Int,
    val name: String,
    val surname: String
)
