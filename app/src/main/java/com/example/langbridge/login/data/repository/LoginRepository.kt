package com.example.langbridge.login.data.repository

import com.example.langbridge.login.data.models.LoginResponse


interface LoginRepository {

    suspend fun login(email: String, password: String): LoginResponse
}