// FULL MODERNIZED MESSAGE SCREEN UI

package com.example.langbridge.messages.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.util.Base64
import android.view.MotionEvent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.langbridge.R
import com.example.langbridge.Screens
import com.example.langbridge.messages.data.models.Message
import com.example.langbridge.messages.data.models.MessageType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    navController: NavController,
    message: Screens.Messages,
    viewModel: MessageViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }
    }

    LaunchedEffect(key1 = false) {
        viewModel.setArgs(message)
        viewModel.createConversation()
        viewModel.getMessageList()
        viewModel.startListening()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(message.receiverName ?: "", style = MaterialTheme.typography.titleLarge)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        MessagesListView(paddingValues, viewModel)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessagesListView(paddingValues: PaddingValues, viewModel: MessageViewModel) {
    var messageText by remember { mutableStateOf("") }
    val messageList by viewModel.messageList
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val isRecording by viewModel.isRecording
    val context = LocalContext.current
    var pressStartTime by remember { mutableLongStateOf(0L) }
    var longPressJob by remember { mutableStateOf<Job?>(null) }

    Column(modifier = Modifier.fillMaxSize().imePadding().padding(paddingValues)) {

        if (isRecording) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.voice_note_start),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                messageList?.let { messages ->
                    items(messages) { message ->
                        message?.let {
                            when {
                                it.isVoiceMessage -> VoiceMessageView(it)
                                it.getMessageType() == MessageType.SENDER -> ReceiverMessageView(it)
                                else -> SenderMessageView(it)
                            }
                        }
                    }
                }
            }
        }

        LaunchedEffect(messageList) {
            messageList?.size?.let { size ->
                if (size > 0) coroutineScope.launch {
                    listState.scrollToItem(size - 1)
                }
            }
        }

        // Input
        Surface(tonalElevation = 3.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp)),
                    placeholder = { Text(stringResource(id = R.string.type_message)) },
                    maxLines = 4,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                FloatingActionButton(
                    modifier = Modifier
                        .size(48.dp)
                        .pointerInteropFilter { event ->
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    pressStartTime = System.currentTimeMillis()
                                    longPressJob = coroutineScope.launch {
                                        delay(500L)
                                        if (ContextCompat.checkSelfPermission(
                                                context, Manifest.permission.RECORD_AUDIO
                                            ) == PackageManager.PERMISSION_GRANTED
                                        ) viewModel.startRecording()
                                    }
                                    true
                                }
                                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                    longPressJob?.cancel()
                                    val duration = System.currentTimeMillis() - pressStartTime
                                    if (duration >= 500L) {
                                        if (viewModel.isRecording.value) viewModel.stopAndSendRecording()
                                    } else {
                                        if (!viewModel.isRecording.value && messageText.isNotEmpty()) {
                                            viewModel.sendNewTextMessage(messageText)
                                            messageText = ""
                                            keyboardController?.hide()
                                        }
                                    }
                                    true
                                }
                                else -> false
                            }
                        },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = {}
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun VoiceMessageView(message: Message) {
    val context = LocalContext.current
    val audioBytes = message.audioContent?.let { Base64.decode(it, Base64.DEFAULT) }
    val tempFile = remember { File.createTempFile("temp_audio_", ".3gp", context.cacheDir).apply {
        writeBytes(audioBytes ?: byteArrayOf())
    } }
    val mediaPlayer = remember { MediaPlayer() }
    val isPlaying = remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
            tempFile.delete()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.getMessageType() == MessageType.SENDER) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .widthIn(0.dp, 280.dp)
                .animateContentSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = {
                    if (!isPlaying.value) {
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(tempFile.absolutePath)
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        isPlaying.value = true
                        mediaPlayer.setOnCompletionListener { isPlaying.value = false }
                    } else {
                        mediaPlayer.stop()
                        isPlaying.value = false
                    }
                }) {
                    Icon(
                        imageVector = if (isPlaying.value) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause"
                    )
                }
                Text("${message.durationSeconds ?: 0} sec")
                Spacer(Modifier.weight(1f))
                FakeWaveform()
            }
        }
    }
}

@Composable
fun FakeWaveform() {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(20) {
            val height = remember { (4..20).random().dp }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(height)
                    .background(MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }
    }
}

@Composable
fun SenderMessageView(message: Message?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.widthIn(0.dp, 280.dp)
        ) {
            Text(
                text = message?.message.orEmpty(),
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ReceiverMessageView(message: Message?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.widthIn(0.dp, 280.dp)
        ) {
            Text(
                text = message?.message.orEmpty(),
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
