package com.example.langbridge.messages.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.langbridge.messages.data.models.MessageResponse
import com.example.langbridge.messages.data.repository.MessageRepository
import com.example.langbridge.messages.data.repository.MessageRepositoryImpl

class MessageViewModel: ViewModel() {
    private val Repository: MessageRepository = MessageRepositoryImpl()
    private val messageResponse = mutableStateOf<MessageResponse?>(null)

    init {

    }



}