package com.example.langbridge.login.data.models

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
