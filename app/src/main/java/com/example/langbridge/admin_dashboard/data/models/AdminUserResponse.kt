package com.example.langbridge.admin_dashboard.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminUserResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("interactions") val interactions: List<AdminUser>? = null
)

@Serializable
data class AdminUser(
    @SerialName("user_id") val userId: String,
    @SerialName("user_name") val userName: String,
    @SerialName("email") val email: String,
    @SerialName("contacts") val contacts: List<AdminContact>
)


@Serializable
data class AdminContact(
    @SerialName("contact_id") val contactId: String,
    @SerialName("contact_name") val contactName: String,
    @SerialName("contact_email") val contactEmail: String
)

