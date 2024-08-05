package com.example.langbridge.messages.ui



import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.langbridge.UserInfo
import com.example.langbridge.messages.data.models.Message
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.langbridge.messages.data.models.MessageType


@Composable
fun MessageScreen(navController: NavController, id:String?, viewModel: MessageViewModel = viewModel()) {
    LaunchedEffect(key1 = false) {
        viewModel.getMessageList(id)
    }

    Scaffold {
        MessagesListView(it, viewModel)
    }

}

@Composable
fun MessagesListView(it: PaddingValues, viewModel: MessageViewModel) {
    var savedtext by remember { mutableStateOf("") }
    val messageList by viewModel.messageList
    Column {
        LazyColumn(
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

        //Message Entry Widget
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            TextField(
                value = savedtext,
                onValueChange = { savedtext = it },
                modifier = Modifier.weight(0.8f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                viewModel.sendNewMessage(savedtext)
                savedtext = ""
            }) {
                Box(
                    modifier = Modifier.fillMaxWidth(0.2f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Send")
                }
            }
        }

    }
}

@Composable
fun SenderMessageView(message: Message?) {
    Text(
        text = message?.message?:"",
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Green
    )
}

@Composable
fun ReceiverMessageView(message: Message?) {
    Text(
        text = message?.message?:"",
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Red
    )
}
