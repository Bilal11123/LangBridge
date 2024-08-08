package com.example.langbridge.users.data.repository

import com.example.langbridge.ApiEndpoints
import com.example.langbridge.AppHttpClient
import com.example.langbridge.users.data.models.UserResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.serialization.json.Json

class UserRepositoryImpl : UserRepository {
    private val httpClient = AppHttpClient()

    override suspend fun getUsers(id: String?): UserResponse {
        return try {
            val response: HttpResponse = httpClient.post(
                ApiEndpoints.USER_LIST,
                Parameters.build {
                    append("id", id ?: "")

                })
            val responseBody = response.bodyAsText()

            // Attempt to parse JSON response
            try {
                Json.decodeFromString<UserResponse>(responseBody)
            } catch (e: Exception) {
                // If parsing fails, treat the response as a plain text message
                UserResponse(users = emptyList())
            }
        } catch (e: Exception) {
            UserResponse(users = emptyList())
        }
    }
}