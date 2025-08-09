package com.example.langbridge

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.langbridge.admin_dashboard.ui.AdminDashboardScreen
import com.example.langbridge.admin_login.ui.AdminLoginScreen
import com.example.langbridge.chatbot.ChatPage
import com.example.langbridge.contacts.ui.ContactScreen
import com.example.langbridge.login.data.repository.GoogleAuthUiClient
import com.example.langbridge.login.ui.LoginScreen
import com.example.langbridge.login.ui.LoginViewModel
import com.example.langbridge.messages.ui.MessageScreen
import com.example.langbridge.settings.ui.SettingsScreen
import com.example.langbridge.signup.ui.SignupScreen
import com.example.langbridge.splash_screen.SplashScreen
import com.example.langbridge.users.ui.UserScreen
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity() {
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext),
            auth = auth
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "splash_screen") {
                composable("splash_screen"){
                    SplashScreen(navController)
                }
                composable("login") {
                    val viewModel = viewModel<LoginViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { result ->
                            if (result.resultCode == RESULT_OK){
                                lifecycleScope.launch {
                                    val signInResult = googleAuthUiClient.getSignInResultFromIntent(
                                        intent = result.data ?: return@launch
                                    )
                                    viewModel.onSignInResult(signInResult)
                                }
                            }
                        }
                    )

                    LaunchedEffect(key1 = state.isSignInSuccessful) {
                        if (state.isSignInSuccessful){
                            Toast.makeText(
                                applicationContext,
                                "Google Sign in Successful",
                                Toast.LENGTH_LONG
                            ).show()
//                            Toast.makeText(
//                                applicationContext,
//                                googleAuthUiClient.getSignedInUser()?.userEmail ?: "unknown",
//                                Toast.LENGTH_LONG
//                            ).show()
//                            coroutineScope.launch {
//                                viewModel.login(googleAuthUiClient.getSignedInUser()?.userEmail ?: "unknown",
//                                    password)
//                            }
                        }
                    }

                    LoginScreen(
                        state = state,
                        onGoogleSignInClick = {
                            lifecycleScope.launch {
                                val signInIntentSender = googleAuthUiClient.signIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        },
                        googleAuthUiClient,
                        navController
                    )
//                    LoginScreen(navController)
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
                composable("chatbot") {
                    ChatPage(modifier = Modifier.padding(),navController)
                }
                composable("signup") {
                    SignupScreen(navController)
                }
                composable("admin_login"){
                    AdminLoginScreen(navController)
                }
                composable("admin_dashboard") {
                    AdminDashboardScreen(navController)
                }
                composable("settings_screen"){
                    SettingsScreen(navController)
                }

            }
        }


    }
}
