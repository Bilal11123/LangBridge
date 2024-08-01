package com.example.langbridge.messages.data.repository

import com.example.langbridge.messages.data.models.MessageResponse

interface MessageRepository {
    suspend fun getMessageList(id: String?): MessageResponse
}