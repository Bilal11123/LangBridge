package com.example.langbridge.messages.data.repository

import com.example.langbridge.messages.data.models.ConversationIdResponse
import com.example.langbridge.messages.data.models.MessageResponse

interface MessageRepository {
    suspend fun getMessageList(id: String?, user_id: String?): MessageResponse
    suspend fun createConversation(receiverId: String?): ConversationIdResponse
}