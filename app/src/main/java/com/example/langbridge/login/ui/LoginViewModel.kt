package com.example.langbridge.login.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.login.data.models.LoginResponse
import com.example.langbridge.login.data.models.SignInResult
import com.example.langbridge.login.data.models.SignInState
import com.example.langbridge.login.data.repository.LoginRepository
import com.example.langbridge.login.data.repository.LoginRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState() {
        _state.update { SignInState() }
    }


    var name = mutableStateOf("")
    var error = mutableStateOf(false)
    var message = mutableStateOf("")
    var email = mutableStateOf("ashhad@gmail.com")
    var password = mutableStateOf("ashhad")
    var repository: LoginRepository = LoginRepositoryImpl()
        private set

    fun rebuildRepository() {
        repository = LoginRepositoryImpl()
    }
    private val _loginResponse = mutableStateOf<LoginResponse?>(null)
    val loginResponse: State<LoginResponse?> = _loginResponse

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.login(email, password)
            withContext(Dispatchers.Main) {
                _loginResponse.value = response
            }
        }
    }

    fun login_oauth(email: String, name: String){
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.login_oauth(email, name)
            withContext(Dispatchers.Main) {
                _loginResponse.value = response
            }
        }
    }


    fun showError(errorMessage: String) {
        error.value = true
        message.value = errorMessage
    }

    fun resetError() {
        error.value = false
        message.value = ""
    }

    fun resetLoginResponse() {
        _loginResponse.value = null
    }

    fun resetStates() {
        resetError()
        resetLoginResponse()
    }


    /* suspend fun logout(email: String) {
        try {
            val response: HttpResponse = httpClient.post("http://$ip:12344/logout/") {
                contentType(io.ktor.http.ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(Parameters.build {
                    append("email", email) // Replace with actual user email or identifier
                }))
            }
            val responseBody = response.bodyAsText()
            println("Logout response: $responseBody")  // Debugging output
        } catch (e: Exception) {
            println("Error during logout: ${e.localizedMessage}")
        }
    }*/
}