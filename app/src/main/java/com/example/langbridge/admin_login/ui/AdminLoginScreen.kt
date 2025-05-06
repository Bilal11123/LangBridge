package com.example.langbridge.admin_login.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.langbridge.UserInfo
import com.example.langbridge.common.ChangeIPDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    navController: NavHostController,
    viewModel: AdminLoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val email by viewModel.email
    val password by viewModel.password
    val loginResponse by viewModel.loginResponse
    val error by viewModel.error
    val message by viewModel.message

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(loginResponse) {
        loginResponse?.let {
            if (it.status == "success") {
                UserInfo.id = it.id
                UserInfo.email = email
                UserInfo.name = it.name
                UserInfo.language = it.language
                UserInfo.user_type = it.user_type

                val destination = if (it.user_type == "admin") "admin_dashboard" else "contacts"
                navController.navigate(destination) {
                    popUpTo("login") { inclusive = true }
                }

                viewModel.resetStates()
            } else {
                viewModel.resetLoginResponse()
                viewModel.showError("Invalid Credentials")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }

        if (showDialog) {
            ChangeIPDialog(
                onDismiss = { showDialog = false },
                onIpEntered = {
                    com.example.langbridge.ip = it
                    com.example.langbridge.socketIp = it
                    viewModel.rebuildRepository()
                }
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Admin Login",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.email.value = it },
                    label = { Text("Admin Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.password.value = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(14.dp)
                )

                if (error) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Login", style = MaterialTheme.typography.titleMedium)
                }

                TextButton(
                    onClick = {
                        viewModel.resetStates()
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to user login")
                }
            }
        }
    }
}

