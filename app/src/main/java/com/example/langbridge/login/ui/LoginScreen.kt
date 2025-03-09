package com.example.langbridge.login.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
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
import com.example.langbridge.Screens
import com.example.langbridge.UserInfo
import com.example.langbridge.login.data.models.SignInState
import com.example.langbridge.login.data.models.UserData
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
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }


    val email by loginVM.email
    val password by loginVM.password
    val message by loginVM.message
    val error by loginVM.error
    val loginResponse by loginVM.loginResponse
    val coroutineScope = rememberCoroutineScope()
    val state by loginVM.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful){
            Toast.makeText(
                context,
                googleAuthUiClient.getSignedInUser()?.userEmail ?: "unknown",
                Toast.LENGTH_LONG
            ).show()
            coroutineScope.launch {
                loginVM.login_oauth(
                    googleAuthUiClient.getSignedInUser()?.userEmail.toString()
                )
            }
//                            coroutineScope.launch {
//                                viewModel.login(googleAuthUiClient.getSignedInUser()?.userEmail ?: "unknown",
//                                    password)
//                            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo or App Name
                Text(
                    text = "LangBridge",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = "Welcome Back",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email TextField
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = email,
                    onValueChange = { loginVM.email.value = it },
                    label = { Text("Email") },
                    isError = error,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Password TextField
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = { loginVM.password.value = it },
                    label = { Text("Password") },
                    isError = error,
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        // Handle keyboard done action
                    }),
                    shape = RoundedCornerShape(12.dp)
                )

                if (error) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = TextStyle(fontSize = 14.sp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Login Button
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            loginVM.showError("Email and Password cannot be empty")
                        } else {
                            coroutineScope.launch {
                                loginVM.login(email, password)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Login",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )
                }

                // Sign Up Button
                TextButton(
                    onClick = { Toast.makeText(
                            context,
                            googleAuthUiClient.getSignedInUser()?.username ?: "unknown",
                            Toast.LENGTH_LONG
                        ).show()
                              },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Show Username",
                        style = TextStyle(fontSize = 14.sp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Button(onClick = onGoogleSignInClick) {
                        Text(text = "Sign In With Google")
                    }
                }
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ){
//                    Button(onClick = {
//                        Toast.makeText(
//                            context,
//                            googleAuthUiClient.getSignedInUser()?.username ?: "unknown",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }) {
//                        Text(text = "Show username")
//                    }
//                }

            }
        }

        // Handle login response
        loginResponse?.let {
            if (it.status == "success") {
                UserInfo.id = it.id
                if (state.isSignInSuccessful){
                    UserInfo.email = googleAuthUiClient.getSignedInUser()?.userEmail
                }else {
                    UserInfo.email = email
                }
                UserInfo.email = email
                UserInfo.language = it.language
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