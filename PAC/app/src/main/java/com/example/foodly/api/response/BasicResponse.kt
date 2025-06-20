package com.example.foodly.api.response

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null
)

