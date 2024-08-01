package com.example.langbridge.messages.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message (
    @SerialName("id") val id: String?,
    @SerialName("message") val message: String?,
    @SerialName("sender_id") val senderId: String?
)

@Serializable
data class MessageResponse (
    @SerialName("messages") val messages: List<Message>?
)

