package com.example.langbridge.users.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User (
    @SerialName("name") val name: String?,
    @SerialName("id") val id: String?
)

@Serializable
data class UserResponse(
    @SerialName("users") val users: List<User>?
)