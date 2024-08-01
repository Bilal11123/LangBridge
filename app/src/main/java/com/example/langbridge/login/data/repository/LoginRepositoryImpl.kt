package com.example.langbridge.login.data.repository

import com.example.langbridge.ApiEndpoints
import com.example.langbridge.AppHttpClient
import com.example.langbridge.ip
import com.example.langbridge.login.data.models.LoginResponse
import com.example.langbridge.port
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.*
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class LoginRepositoryImpl:LoginRepository {
    private val httpClient = AppHttpClient()

    override suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val response: HttpResponse = httpClient.post(
                ApiEndpoints.LOGIN,
                Parameters.build {
                    append("email", email)
                    append("password", password)

                })
            val responseBody = response.bodyAsText()

            // Attempt to parse JSON response
            try {
                Json.decodeFromString<LoginResponse>(responseBody)
            } catch (e: Exception) {
                // If parsing fails, treat the response as a plain text message
                LoginResponse(status = "error",  name = "Unknown", )
            }
        } catch (e: Exception) {
            LoginResponse(status = "error", name = "Unknown")
        }
    }
}