package com.example.langbridge.contacts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.langbridge.Screens
import com.example.langbridge.UserInfo
import com.example.langbridge.common.ChooseLanguageDialog
import com.example.langbridge.contacts.data.models.Contact


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ContactScreen(navController: NavController, viewModel: ContactViewModel = viewModel()) {
    val contactResponse by viewModel.contactResponse
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        ChooseLanguageDialog { language ->
            viewModel.changeLanguage(language)
            showDialog = false
        }
    }
    val pullRefreshState =
        rememberPullRefreshState(refreshing = viewModel.isLoading.value, onRefresh = {
            viewModel.getContacts()
        })

    Scaffold(topBar = {
        TopAppBar(colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue), title = {
            Text(
                text = "Hello, ${UserInfo.name}", style = TextStyle(
                    fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold
                ), textAlign = TextAlign.Left
            )
        }, actions = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(content = {
            Icon(
                imageVector = Icons.Filled.Add, contentDescription = "Add Users", tint = Color.Black
            )
        }, onClick = { navController.navigate("users") })
    }) {
        Box(
            modifier = Modifier
                .padding(it)
                .pullRefresh(pullRefreshState)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(contactResponse?.contacts?.size ?: 0) { index ->
                    ContactItem(contactResponse?.contacts?.get(index), onItemClick = { contact ->
                        navController.navigate(
                            Screens.Messages(
                                contactId = contact?.contactId,
                                receiverId = "default",
                                receiverName = contact?.name
                            )
                        )
                    })
                }
            }

            PullRefreshIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                refreshing = viewModel.isLoading.value,
                state = pullRefreshState
            )

        }
    }
}

@Composable
fun RadioButtonGroup(
    options: Map<String, String>, selectedOption: String?, onOptionSelected: (String) -> Unit
) {
    Column {
        options.entries.forEach { option ->
            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (option.key == selectedOption),
                    onClick = { onOptionSelected(option.key) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(option.value)
            }
        }
    }
}


@Composable
fun ContactItem(contact: Contact?, onItemClick: (Contact?) -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .border(2.dp, Color.DarkGray)
        .background(Color.LightGray)
        .padding(8.dp)
        .clickable {
            onItemClick.invoke(contact)
        }) {
        Column {
            Text(
                text = contact?.name ?: "", style = TextStyle(
                    fontSize = 18.sp
                ), textAlign = TextAlign.Left
            )
            Text(
                text = contact?.lastMessage ?: "", style = TextStyle(
                    fontSize = 12.sp
                ), textAlign = TextAlign.Left
            )
        }
    }
}

