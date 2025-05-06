package com.example.langbridge.signup.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    navController: NavHostController,
    signupViewModel: SignupViewModel = viewModel()
) {
    val context = LocalContext.current

    val username by signupViewModel.username
    val email by signupViewModel.email
    val password by signupViewModel.password
    val message by signupViewModel.message
    val isLoading by signupViewModel.isLoading
    val signupResponse by signupViewModel.signupResponse
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("en", "ru", "zh", "fr")
    val selectedLanguage by signupViewModel.language

    LaunchedEffect(signupResponse) {
        signupResponse?.let {
            if (it.status == "Signup successful") {
                Toast.makeText(context, "Signup successful!", Toast.LENGTH_LONG).show()
                navController.navigate("login") {
                    popUpTo("signup") { inclusive = true }
                }
            } else {
                Toast.makeText(context, it.status, Toast.LENGTH_LONG).show()
                signupViewModel.reset()
            }
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
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Create Account", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = username,
                    onValueChange = { signupViewModel.username.value = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { signupViewModel.email.value = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { signupViewModel.password.value = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Select Language", style = MaterialTheme.typography.bodyMedium)

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = selectedLanguage.uppercase())
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language.uppercase()) },
                                onClick = {
                                    signupViewModel.language.value = language
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (message.isNotEmpty()) {
                    Text(text = message, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = { signupViewModel.signup() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isLoading) "Signing Up..." else "Sign Up")
                }

                TextButton(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Already have an account? Login", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
