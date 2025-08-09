package com.example.langbridge.chatbot

import android.Manifest
import androidx.core.app.ActivityCompat
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.langbridge.R
import com.example.langbridge.UserInfo
import com.example.langbridge.ui.theme.ColorModelMessage
import com.example.langbridge.ui.theme.ColorUserMessage
import com.example.langbridge.ui.theme.Purple80

@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    context: Context = LocalContext.current,
    viewModel: ChatViewModel = remember { ChatViewModel(context) }
) {
    // Clean up when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopSpeaking()
        }
    }
    Column(
        modifier = modifier.fillMaxSize()  // Add fillMaxSize here
    ) {
        AppHeader(navController)
        MessageList(
            modifier = Modifier.weight(1f),  // This ensures it takes all available space
            messageList = viewModel.messageList,
            viewModel = viewModel
        )
        MessageInput(
            onMessageSend = {
                viewModel.sendMessage(it)
            }
        )
    }
}


@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messageList: List<MessageModel>,
    viewModel: ChatViewModel
) {
    Box(modifier = modifier) {  // Wrap in Box to ensure proper constraints
        if (messageList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(60.dp),
                    painter = painterResource(id = R.drawable.baseline_question_answer_24),
                    contentDescription = "Icon",
                    tint = Purple80,
                )
                Text(text = stringResource(id = R.string.chatbot_placeholder), fontSize = 22.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),  // Ensure LazyColumn fills available space
                reverseLayout = true
            ) {
                items(messageList.reversed()) { message ->
                    MessageRow(
                        messageModel = message,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel, viewModel: ChatViewModel) {
    val isModel = messageModel.role == "model"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isModel) {
            IconButton(
                modifier = Modifier.size(36.dp),  // Fixed size for consistency
                onClick = { viewModel.speak(messageModel.message) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_volume_up_24),
                    contentDescription = "Speak",
                    tint = MaterialTheme.colorScheme.primary  // Make sure it's visible
                )
            }
        } else {
            Spacer(modifier = Modifier.size(36.dp))  // Maintain consistent spacing
        }

        Box(
            modifier = Modifier.weight(1f)  // Take remaining space
        ) {
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isModel) ColorModelMessage else ColorUserMessage)
                    .padding(16.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = messageModel.message,
                        fontWeight = FontWeight.W500,
                        color = Color.White
                    )
                }
            }
        }
    }
}






@Composable
fun AppHeader(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 8.dp),  // Add vertical padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier.padding(start = 4.dp)  // Add some start padding
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White  // Ensure icon is visible against primary color
            )
        }

        Text(
            modifier = Modifier
                .weight(1f)  // Take remaining space
                .padding(horizontal = 8.dp),  // Add horizontal padding
            text = stringResource(id = R.string.chatbot_header),
            color = Color.White,
            fontSize = 22.sp
        )
    }
}




// Convert UserInfo.language to Android speech recognition codes
private fun getSpeechRecognitionLanguageCode(): String {
    return when (UserInfo.language) {
        "en" -> "en-US"  // English
        "ru" -> "ru-RU"  // Russian
        "zh" -> "zh-CN"  // Chinese
        "fr" -> "fr-FR"  // French
        else -> "en-US"  // Default fallback
    }
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { isListening = false }

                override fun onError(error: Int) {
                    isListening = false
                    val errorMsg = when (error) {
                        SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED ->
                            "Language ${UserInfo.language} not supported"
                        else -> "Error: ${getErrorText(error)}"
                    }
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: Bundle?) {
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                        if (it.isNotEmpty()) message = it[0]
                    }
                    isListening = false
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = { message = it }
        )

        // Microphone Button
        IconButton(
            onClick = {
                if (isListening) {
                    speechRecognizer.stopListening()
                } else {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        speechRecognizer.startListening(
                            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE,
                                    getSpeechRecognitionLanguageCode()
                                )
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                            }
                        )
                        isListening = true
                    } else {
                        ActivityCompat.requestPermissions(
                            context as android.app.Activity,
                            arrayOf(Manifest.permission.RECORD_AUDIO),
                            1
                        )
                    }
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_mic_24),
                contentDescription = "Speak",
                tint = if (isListening) Color.Red else MaterialTheme.colorScheme.primary
            )
        }

        // Send Button
        IconButton(
            onClick = {
                if (message.isNotEmpty()) {
                    onMessageSend(message)
                    message = ""
                }
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send"
            )
        }
    }
}

// Error code to text conversion
private fun getErrorText(errorCode: Int): String {
    return when (errorCode) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio error"
        SpeechRecognizer.ERROR_CLIENT -> "Client error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "No microphone permission"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
        SpeechRecognizer.ERROR_NO_MATCH -> "No speech match"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
        SpeechRecognizer.ERROR_SERVER -> "Server error"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
        else -> "Unknown error"
    }
}
