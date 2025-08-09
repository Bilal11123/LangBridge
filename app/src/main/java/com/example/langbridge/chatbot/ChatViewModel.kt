package com.example.langbridge.chatbot

import android.speech.tts.TextToSpeech
import android.content.Context
import java.util.Locale
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.UserInfo
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch


class ChatViewModel(context: Context) : ViewModel(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var ttsInitialized = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        ttsInitialized = (status == TextToSpeech.SUCCESS)
        if (ttsInitialized) {
            setLanguage(UserInfo.language ?: "en") // Use user's preferred language
        }
    }

    private fun setLanguage(languageCode: String) {
        val locale = when (languageCode) {
            "ru" -> Locale("ru", "RU")
            "zh" -> Locale("zh", "CN")
            "fr" -> Locale.FRENCH
            else -> Locale.US // Default to English
        }
        tts.language = locale
    }

    fun speak(text: String) {
        if (ttsInitialized) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onCleared() {
        tts.shutdown()
        super.onCleared()
    }

    // In ChatViewModel.kt
    fun stopSpeaking() {
        if (ttsInitialized) {
            tts.stop() // Stop current speech
        }
    }

    val messageList by lazy {
        mutableStateListOf<MessageModel>()
    }

    val generativeModel : GenerativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = Constant.apiKey
    )

    fun sendMessage(question : String){
        viewModelScope.launch {

            try{
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role){ text(it.message) }
                    }.toList()
                )

                messageList.add(MessageModel(question,"user"))
                messageList.add(MessageModel("Typing....","model"))

                val response = chat.sendMessage(question)
                val responseText = response.text.toString()
                messageList.removeAt(messageList.lastIndex)
                messageList.add(MessageModel(response.text.toString(),"model"))

                speak(responseText)
            }catch (e : Exception){
                messageList.removeAt(messageList.lastIndex)
                messageList.add(MessageModel("Error : "+e.message.toString(),"model"))
            }
        }
    }
}