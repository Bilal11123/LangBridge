package com.example.langbridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.langbridge.contacts.ui.ContactScreen
import com.example.langbridge.login.ui.LoginScreen
import com.example.langbridge.login.ui.LoginViewModel
import com.example.langbridge.messages.ui.MessageScreen


@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val viewModel : LoginViewModel = viewModel()
            LoginScreen(navController,viewModel)
        }
        composable("contacts") {
            ContactScreen(navController)
        }
        composable("messages/{contact_id}",
            arguments = listOf(navArgument("contact_id") { type = NavType.StringType })
        ) {
            val contactId= it.arguments?.getString("contact_id")
            MessageScreen(navController,contactId)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContent{
                App()
            }
    }
}

