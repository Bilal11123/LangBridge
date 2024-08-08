package com.example.langbridge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.langbridge.contacts.ui.ContactScreen
import com.example.langbridge.login.ui.LoginScreen
import com.example.langbridge.messages.ui.MessageScreen
import com.example.langbridge.users.ui.UserScreen


@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("contacts") {
            ContactScreen(navController)
        }
        composable("users") {
            UserScreen(navController)
        }
        composable<Screens.Messages> {
            val message = it.toRoute<Screens.Messages>()
            MessageScreen(navController, message)
        }
    }
}


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }


    }
}

