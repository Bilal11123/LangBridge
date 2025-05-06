package com.example.langbridge.signup.data.repository

import com.example.langbridge.ApiEndpoints
import com.example.langbridge.AppHttpClient
import com.example.langbridge.signup.data.models.SignupResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.serialization.json.Json

class SignupRepository {

    private val httpClient = AppHttpClient()

    suspend fun signup(username: String, email: String, password: String, language: String): SignupResponse {
        return try {
            val response = httpClient.post(
                ApiEndpoints.SIGNUP,
                Parameters.build {
                    append("username", username)
                    append("email", email)
                    append("password", password)
                    append("language", language) // Default language
                }
            )
            val responseBody = response.bodyAsText()
            Json.decodeFromString(responseBody)
        } catch (e: Exception) {
            SignupResponse(status = "Signup failed: ${e.localizedMessage}")
        }
    }
}
