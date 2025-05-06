package com.example.langbridge.login.data.repository

import com.example.langbridge.login.data.models.LoginResponse


interface LoginRepository {

    suspend fun login(email: String, password: String): LoginResponse
    suspend fun login_oauth(email: String, name: String): LoginResponse
}