package com.example.langbridge.messages.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.SocketManager
import com.example.langbridge.UserInfo
import com.example.langbridge.messages.data.models.Message
import com.example.langbridge.messages.data.models.SocketMessage
import com.example.langbridge.messages.data.repository.MessageRepository
import com.example.langbridge.messages.data.repository.MessageRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId

class MessageViewModel(context: Application): AndroidViewModel(context) {
    private val repository: MessageRepository = MessageRepositoryImpl()

    val messageList = mutableStateOf<List<Message?>?>(null)
    var error = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    private var conversationId: String? = ""
    private val socketManager =  SocketManager(context)

    init {
        startListening()
    }

    /*private fun startListening(){
        viewModelScope.launch {
            delay(2000)
            socketManager.startListening { message ->
                // Handle received messages
                messageList.value = messageList.value?.plus(message)
            }
        }
    }*/

    private fun startListening() {
        viewModelScope.launch {
            // Initialize the SocketManager and set the callback for incoming messages
            socketManager.setOnMessageReceivedCallback { receivedMessage ->

                // Handle received messages
                messageList.value = messageList.value?.plus(receivedMessage)
            }

            // Connect to the WebSocket server
            socketManager.connect()
        }
    }

    fun stopListening(){
        viewModelScope.launch {
            socketManager.stopListening()
        }

    }


    fun getMessageList(id:String?){
        conversationId = id
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMessageList(id)
                withContext(Dispatchers.Main) {
                    messageList.value = response.messages
                }
            } catch (e: Exception) {
                // Handle exceptions
                withContext(Dispatchers.Main){
                    error.value = "Failed to fetch contacts: ${e.message}"
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }

    fun sendNewMessage(text: String) {
        val message = Message(
            id = ObjectId().toHexString(),
            message = text,
            senderId = UserInfo.id,
            locale = UserInfo.language
        )

        val socketmessage = SocketMessage(
            message = message,
            conversationId = conversationId
        )

        messageList.value = messageList.value?.plus(message)

        //send message over tcp
        viewModelScope.launch(Dispatchers.IO) {
            socketManager.sendMessage(socketmessage)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}