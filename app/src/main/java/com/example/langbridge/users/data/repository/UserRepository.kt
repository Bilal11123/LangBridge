package com.example.langbridge.users.data.repository

import com.example.langbridge.users.data.models.UserResponse

interface UserRepository {
    suspend fun getUsers(id : String?): UserResponse
}