package com.example.foodly.api.request

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsRequest(
    val id_user: String
)
