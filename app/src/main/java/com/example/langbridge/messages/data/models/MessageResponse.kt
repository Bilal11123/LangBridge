package com.example.langbridge.messages.data.models

import com.example.langbridge.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Message (
    @SerialName("id") val id: String?,
    @SerialName("message") val message: String?,
    @SerialName("sender_id") val senderId: String?,
    @SerialName("locale") val locale: String?
) {
    fun getMessageType(): MessageType {
        return if (senderId == UserInfo.id) {
            MessageType.SENDER
        } else {
            MessageType.RECEIVER
        }
    }
}

@Serializable
data class SocketMessage(
    @SerialName("message") val message: Message?,
    @SerialName("conversation_id") val conversationId: String?
)

@Serializable
data class MessageResponse (
    @SerialName("messages") val messages: List<Message>?
)

