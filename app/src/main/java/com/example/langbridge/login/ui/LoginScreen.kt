package com.example.langbridge.login.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.langbridge.UserInfo
import com.example.langbridge.common.ChangeIPDialog
import com.example.langbridge.login.data.models.SignInState
import com.example.langbridge.login.data.repository.GoogleAuthUiClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: SignInState,
    onGoogleSignInClick: () -> Unit,
    googleAuthUiClient: GoogleAuthUiClient,
    navController: NavHostController,
    loginVM: LoginViewModel = viewModel(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val email by loginVM.email
    val password by loginVM.password
    val message by loginVM.message
    val error by loginVM.error
    val loginResponse by loginVM.loginResponse
    val signInState by loginVM.state.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.signInError) {
        state.signInError?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    LaunchedEffect(signInState.isSignInSuccessful) {
        if (signInState.isSignInSuccessful) {
            Toast.makeText(context, googleAuthUiClient.getSignedInUser()?.userEmail ?: "unknown", Toast.LENGTH_LONG).show()
            coroutineScope.launch {
                loginVM.login_oauth(
                    googleAuthUiClient.getSignedInUser()?.userEmail.orEmpty(),
                    googleAuthUiClient.getSignedInUser()?.username.orEmpty()
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Settings Icon
        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }

        // IP Dialog
        if (showDialog) {
            ChangeIPDialog(
                onDismiss = { showDialog = false },
                onIpEntered = {
                    com.example.langbridge.ip = it
                    com.example.langbridge.socketIp = it
                    loginVM.rebuildRepository()
                }
            )
        }

        // Login Card
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("LangBridge", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Welcome Back",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = { loginVM.email.value = it },
                    label = { Text("Email") },
                    isError = error,
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = { loginVM.password.value = it },
                    label = { Text("Password") },
                    isError = error,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {}),
                    shape = RoundedCornerShape(16.dp)
                )

                if (error) {
                    Text(
                        message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        loginVM.resetStates()
                        if (email.isBlank() || password.isBlank()) {
                            loginVM.showError("Email and Password cannot be empty")
                        } else {
                            coroutineScope.launch {
                                loginVM.login(email, password)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Login", style = MaterialTheme.typography.titleMedium)
                }

                TextButton(
                    onClick = { navController.navigate("admin_login") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Admin Login")
                }

                TextButton(
                    onClick = { navController.navigate("signup") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Up", style = MaterialTheme.typography.bodyMedium)
                }

                OutlinedButton(
                    onClick = onGoogleSignInClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Sign In With Google")
                }
            }
        }

        loginResponse?.let {
            if (it.status == "success") {
                UserInfo.id = it.id
                UserInfo.email = googleAuthUiClient.getSignedInUser()?.userEmail ?: email
                UserInfo.name = googleAuthUiClient.getSignedInUser()?.username ?: it.name
                UserInfo.language = it.language
                UserInfo.user_type = it.user_type

                navController.navigate("contacts") {
                    popUpTo("login") { inclusive = true }
                }

                loginVM.resetStates()
            } else {
                loginVM.resetLoginResponse()
                loginVM.showError("Invalid Credentials")
            }
        }
    }
}

