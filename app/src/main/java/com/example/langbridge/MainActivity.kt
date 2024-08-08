package com.example.langbridge

import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.langbridge.contacts.ui.ContactScreen
import com.example.langbridge.login.ui.LoginScreen
import com.example.langbridge.messages.ui.MessageScreen
import com.example.langbridge.users.ui.UserScreen
import kotlinx.parcelize.Parcelize


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
        composable<Screens.Messages>{
            val message = it.toRoute<Screens.Messages>()
            MessageScreen(navController,message)
        }
    }
}

@Parcelize
data class Test(
    val name: String
):Parcelable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContent{
                App()
            }
    }
}

