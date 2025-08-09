package com.example.langbridge.admin_login.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.langbridge.admin_login.data.AdminLoginRepository
import com.example.langbridge.login.data.models.LoginResponse
import kotlinx.coroutines.launch

class AdminLoginViewModel : ViewModel() {

    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val loginResponse = mutableStateOf<LoginResponse?>(null)
    val error = mutableStateOf(false)
    val message = mutableStateOf("")

    var repository = AdminLoginRepository()
        private set

    fun rebuildRepository() {
        repository = AdminLoginRepository()
    }

    fun login() {
        if (email.value.isBlank() || password.value.isBlank()) {
            showError("Email and Password required")
            return
        }

        viewModelScope.launch {
            val response = repository.loginAdmin(email.value, password.value)
            loginResponse.value = response
        }
    }

    fun showError(msg: String) {
        error.value = true
        message.value = msg
    }

    fun resetLoginResponse() {
        loginResponse.value = null
    }

    fun resetStates() {
        error.value = false
        message.value = ""
        loginResponse.value = null
    }

}
