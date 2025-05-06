package com.example.langbridge.admin_login.data

import com.example.langbridge.ApiEndpoints
import com.example.langbridge.AppHttpClient
import com.example.langbridge.login.data.models.LoginResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.serialization.json.Json

class AdminLoginRepository {
    private val client = AppHttpClient()

    suspend fun loginAdmin(email: String, password: String): LoginResponse {
        return try {
            val response = client.post(ApiEndpoints.ADMIN_LOGIN, Parameters.build {
                append("email", email)
                append("password", password)
            })
            val body = response.bodyAsText()
            Json.decodeFromString(body)
        } catch (e: Exception) {
            LoginResponse(status = "error", name = "Unknown")
        }
    }
}
