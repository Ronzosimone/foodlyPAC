package com.example.foodly.api.response

import kotlinx.serialization.Serializable

@Serializable
data class GetAccountResponse(
    val Id: String,
    val fiscalCode: String,
    val nameLast: String,
    val nameFirst: String,
    val emailAddress: String,
    val mobileNumber: String,
    val birthDate: String
)

@Serializable
data class GetNotificationsResponse(
    val id: String,
    val date: String,
    val subject: String,
    val text: String
)

@Serializable
data class Notification(
    val key: String,
    val day: String,
    val date: String,
    val type: String,
    val description: String
)
