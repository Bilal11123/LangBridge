package com.example.langbridge.loginWithGoogle

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)