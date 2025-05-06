package com.example.langbridge.signup.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.signup.data.repository.SignupRepository
import com.example.langbridge.signup.data.models.SignupResponse
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {

    private val repository = SignupRepository()

    var username = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var message = mutableStateOf("")
    var language = mutableStateOf("en") // default value
    var isLoading = mutableStateOf(false)
    var signupResponse = mutableStateOf<SignupResponse?>(null)

    fun signup() {
        if (username.value.isBlank() || email.value.isBlank() || password.value.isBlank()) {
            message.value = "All fields are required"
            return
        }

        if (!isValidEmail(email.value)) {
            message.value = "Invalid email format"
            return
        }

        isLoading.value = true
        viewModelScope.launch {
            val response = repository.signup(
                username.value,
                email.value,
                password.value,
                language.value
            )
            signupResponse.value = response
            isLoading.value = false
        }
    }

    fun reset() {
        message.value = ""
        signupResponse.value = null
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
