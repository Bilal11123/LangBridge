package com.example.langbridge

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.langbridge.contacts.ui.ContactScreen
import com.example.langbridge.login.ui.LoginScreen
import com.example.langbridge.loginWithGoogle.GoogleAuthUiClient
import com.example.langbridge.loginWithGoogle.GoogleSignInScreen
import com.example.langbridge.loginWithGoogle.ProfileScreen
import com.example.langbridge.loginWithGoogle.SignInViewModel
import com.example.langbridge.messages.ui.MessageScreen
import com.example.langbridge.users.ui.UserScreen
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun App() {

}


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

            NavHost(navController = navController, startDestination = "login") {
                composable("sign_in") {
                    val viewModel = viewModel<SignInViewModel>()
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

                            navController.navigate("profile")
                        }
                    }

                    GoogleSignInScreen(
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
                        }
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        userData = googleAuthUiClient.getSignedInUser(),
                        onSignOut = {
                            lifecycleScope.launch {
                                googleAuthUiClient.signOut()
                                navController.popBackStack()
                            }
                        }
                    )
                }
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


    }
}
