package com.example.langbridge.users.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.langbridge.Screens
import com.example.langbridge.users.data.models.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController, viewModel: UserViewModel = viewModel()) {
    val users by viewModel.users

    Scaffold (
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Blue),
                title = {
                    Text(
                        text = "Users",
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
        LazyColumn(
            modifier = Modifier
                .padding(it)
        ) {
            items(users?.size ?: 0) { index ->
                UserItem(users?.get(index), onItemClick = { user->
                    navController.navigate(
                        Screens.Messages(
                            contactId = "default",
                            receiverId = user?.id,
                            receiverName = user?.name
                        )
                    )                })
            }
        }
    }
}

@Composable
fun UserItem(user: User?, onItemClick: (User?) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(2.dp, Color.DarkGray)
            .background(Color.LightGray)
            .padding(8.dp)
            .clickable {
                onItemClick.invoke(user)
            }
    ) {
        Column{
            Text(
                text = user?.name?:"",
                style = TextStyle(
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Left
            )
        }
    }
}

