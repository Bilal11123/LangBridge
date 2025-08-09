package com.example.langbridge.messages.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.Screens
import com.example.langbridge.SocketManager
import com.example.langbridge.UserInfo
import com.example.langbridge.messages.data.models.AudioRecorder
import com.example.langbridge.messages.data.models.Message
import com.example.langbridge.messages.data.models.SocketMessage
import com.example.langbridge.messages.data.repository.MessageRepository
import com.example.langbridge.messages.data.repository.MessageRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId

class MessageViewModel(context: Application) : AndroidViewModel(context) {
    private val repository: MessageRepository = MessageRepositoryImpl()
    val messageList = mutableStateOf<List<Message?>?>(null)
    var error = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    private var conversationId: String? = "default"
    private var receiverId: String? = "default"
    private val socketManager: SocketManager by lazy { SocketManager(context) }
    private val audioRecorder = AudioRecorder(context)

    var isRecording = mutableStateOf(false)
        private set


    fun setArgs(messages: Screens.Messages) {
        this.conversationId = messages.contactId
        this.receiverId = messages.receiverId
        socketManager.setConversationId(conversationId)
    }

    fun createConversation() {
        if (conversationId == "default") {
            messageList.value = emptyList()
            viewModelScope.launch(Dispatchers.IO) {
                val response = repository.createConversation(receiverId)
                withContext(Dispatchers.Main) {
                    conversationId = response.conversationId
                    socketManager.setConversationId(conversationId)
                    socketManager.connect()
                }
            }
        }
    }

    fun startListening() {
        viewModelScope.launch {
            // Initialize the SocketManager and set the callback for incoming messages
            socketManager.setOnMessageReceivedCallback { receivedMessage ->
                // Handle received messages
                messageList.value = messageList.value?.plus(receivedMessage)
            }
        }
    }

    private fun stopListening() {
        socketManager.stopListening()
    }


    fun getMessageList() {
        if (conversationId == "default")
            return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMessageList(conversationId, UserInfo.id)
                withContext(Dispatchers.Main) {
                    messageList.value = response.messages
                }
            } catch (e: Exception) {
                // Handle exceptions
                withContext(Dispatchers.Main) {
                    error.value = "Failed to fetch messages: ${e.message}"
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

        val socketMessage = SocketMessage(
            message = message,
            conversationId = conversationId
        )

        messageList.value = messageList.value?.plus(message)

        //send message over tcp
        viewModelScope.launch(Dispatchers.IO) {
            socketManager.sendMessage(socketMessage)
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.unregisterConnectivityManager()
        stopListening()
    }

    fun startRecording() {
        isRecording.value = true
        audioRecorder.startRecording()
    }

    fun stopAndSendRecording() {
        val recordingResult = audioRecorder.stopRecording()
        isRecording.value = false
        recordingResult?.let {
            sendAudioMessage(it.base64Audio, it.durationSeconds)
        }
    }

    private fun sendAudioMessage(base64Audio: String, durationSeconds: Int) {
        val message = Message(
            id = ObjectId().toHexString(),
            message = "Voice Message",
            senderId = UserInfo.id,
            locale = UserInfo.language,
            isVoiceMessage = true,
            audioContent = base64Audio,
            durationSeconds = durationSeconds
        )

        val socketMessage = SocketMessage(
            message = message,
            conversationId = conversationId
        )

        messageList.value = messageList.value?.plus(message)

        viewModelScope.launch(Dispatchers.IO) {
            socketManager.sendMessage(socketMessage)
        }
    }

    fun sendNewTextMessage(text: String) {
        val message = Message(
            id = ObjectId().toHexString(),
            message = text,
            senderId = UserInfo.id,
            locale = UserInfo.language,
            isVoiceMessage = false
        )

        val socketMessage = SocketMessage(
            message = message,
            conversationId = conversationId
        )

        messageList.value = messageList.value?.plus(message)

        viewModelScope.launch(Dispatchers.IO) {
            socketManager.sendMessage(socketMessage)
        }
    }

}