package com.example.langbridge.login.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.langbridge.UserInfo
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavHostController, loginVM: LoginViewModel = viewModel()) {

    val email by loginVM.email
    val password by loginVM.password
    val message by loginVM.message
    val error by loginVM.error
    val loginResponse by loginVM.loginResponse
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Heading
        Text(
            text = "LangBridge Login", style = TextStyle(
                fontSize = 24.sp, fontWeight = FontWeight.Bold
            )
        )

        //Email text field
        TextField(
            value = email,
            isError = error,
            onValueChange = { loginVM.email.value = it },
            label = { Text("Email") }
        )

        //Password text field
        TextField(value = password,
            isError = error,
            onValueChange = { loginVM.password.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                // Handle action when the 'Done' button is pressed on the keyboard
            }))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            //Forgot Password Button
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Sign Up")
            }

            Spacer(modifier = Modifier.fillMaxWidth(0.2f))

            //Login Button
            Button(onClick = {
                // Handle login action
                if (email.isBlank() || password.isBlank()) {
                    loginVM.showError("Email and Password cannot be empty")
                } else {
                    coroutineScope.launch {
                        loginVM.login(email, password)
                    }
                }
            }) {
                Text(text = "Login")
            }
        }


        if (error) {
            Text(
                text = message,
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp)
            )
        }
    }

    loginResponse?.let {
        if (it.status == "success") {
            UserInfo.id = it.id
            UserInfo.name = it.name
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

