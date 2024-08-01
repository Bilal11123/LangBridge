package com.example.langbridge.contacts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.langbridge.contacts.data.models.Contact


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(navController: NavController, viewModel: ContactViewModel = viewModel()) {
    val contactResponse by viewModel.contactResponse

    Scaffold (
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
                title = {
                    Text(
                        text = viewModel.name.value ?: "",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Left
                    )
                },
                actions = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.Black
                        )
                    }
                }
            )
        },
        floatingActionButton={
            FloatingActionButton(
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Users",
                        tint = Color.Black
                    )
                },
                onClick = { }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it)) {

            Box(
                modifier = Modifier
                    .border(2.dp, Color.DarkGray) // Darken border color
                    .background(Color.LightGray) // Background color
                    .padding(8.dp) // Padding inside the border
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Contacts: ",
                    style = TextStyle(
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Left
                )
            }
            LazyColumn {
                items(contactResponse?.contacts?.size ?: 0) { index ->
                    ContactItem(contactResponse?.contacts?.get(index), onItemClick = { contact ->
                        navController.navigate("messages/${contact?.contactId}")
                    })
                }
            }
        }
    }
}

@Composable
fun ContactItem(contact: Contact?,onItemClick: (Contact?) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(2.dp, Color.DarkGray)
            .background(Color.LightGray)
            .padding(8.dp)
            .clickable {
                onItemClick.invoke(contact)
            }
    ) {
        Column{
            Text(
                text = contact?.name?:"",
                style = TextStyle(
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Left
            )
            Text(
                text = contact?.lastMessage ?: "",
                style = TextStyle(
                    fontSize = 12.sp
                ),
                textAlign = TextAlign.Left
            )
        }
    }
}

