package com.example.langbridge.messages.ui


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.langbridge.Screens
import com.example.langbridge.messages.data.models.Message
import com.example.langbridge.messages.data.models.MessageType
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    navController: NavController,
    message: Screens.Messages,
    viewModel: MessageViewModel = viewModel()
) {
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
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
                title = {
                    Text(
                        text = message.receiverName ?: "",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Left
                    )
                },
            )
        }
    ) {

        MessagesListView(it, viewModel)
    }

}

@Composable
fun MessagesListView(it: PaddingValues, viewModel: MessageViewModel) {
    var savedText by remember { mutableStateOf("") }
    val messageList by viewModel.messageList
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .imePadding()
            .padding(it)
            .fillMaxWidth()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f)
        ) {
            messageList?.let { messages ->
                items(messages) { message ->
                    if (message?.getMessageType() == MessageType.SENDER) {
                        ReceiverMessageView(message)
                    } else {
                        SenderMessageView(message)
                    }
                }
            }
        }

        LaunchedEffect(key1 = false) {
            coroutineScope.launch {
                listState.scrollToItem(messageList?.size ?: 0)
            }
        }

        LaunchedEffect(messageList) {
            coroutineScope.launch {
                listState.scrollToItem(messageList?.size ?: 0)
            }
        }

        //Message Entry Widget
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = savedText,
                onValueChange = { savedText = it },
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        shape = CircleShape,
                        color = Color.Blue.copy(
                            alpha = if (savedText.isEmpty()) 0.3f else 1f
                        )
                    ),

                enabled = savedText.isNotEmpty(),
                onClick = {
                    viewModel.sendNewMessage(savedText)
                    savedText = ""
                    keyboardController?.hide()
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

    }
}

@Composable
fun SenderMessageView(message: Message?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp)

    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .wrapContentSize(Alignment.TopStart)
                .padding(start = 2.dp, end = 40.dp, top = 5.dp, bottom = 5.dp)
                .border(
                    border = BorderStroke(2.dp, Color.Black),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(10.dp)
        ) {
            Text(
                text = message?.message ?: "",
                textAlign = TextAlign.Start,
                color = Color.Blue
            )
        }
    }
}

@Composable
fun ReceiverMessageView(message: Message?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .wrapContentSize(Alignment.TopEnd)
                .padding(start = 40.dp, end = 2.dp, top = 5.dp, bottom = 5.dp)
                .border(
                    border = BorderStroke(2.dp, Color.Black),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(10.dp)
        ) {
            Text(
                text = message?.message ?: "",
                textAlign = TextAlign.Start,
                color = Color.Red
            )
        }
    }
}
