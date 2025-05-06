package com.example.langbridge.login.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val status: String? = null,
    val name: String? = null,
    val id: String? = null,
    val language: String? = null,
    val user_type: String? = "user"  // Optional, with default fallback
)
