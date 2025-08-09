package com.example.langbridge

import androidx.compose.runtime.mutableStateOf


object UserInfo {
    var name = mutableStateOf<String?>("")
    var email: String? = ""
    var id: String? = ""
    var language: String? = ""
    var user_type: String? = ""
}