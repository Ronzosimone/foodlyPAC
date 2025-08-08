package com.example.foodly.api.request


import kotlinx.serialization.Serializable


@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegistrationRequest(
    val name: String,
    val surname: String,
    val email: String,
    val password: String
)


