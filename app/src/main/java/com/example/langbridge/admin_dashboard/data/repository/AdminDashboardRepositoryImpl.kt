package com.example.langbridge.admin_dashboard.data.repository

import com.example.langbridge.ApiEndpoints
import com.example.langbridge.AppHttpClient
import com.example.langbridge.admin_dashboard.data.models.AdminUser
import com.example.langbridge.admin_dashboard.data.models.AdminUserResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.serialization.json.Json

class AdminRepositoryImpl : AdminRepository {
    private val httpClient = AppHttpClient()

    override suspend fun getUserInteractions(): List<AdminUser> {
        return try {
            val response = httpClient.post(
                ApiEndpoints.ADMIN_USER_INTERACTIONS,
                Parameters.build {
                    // You can append any admin-specific ID/token if needed
                    append("auth", "admin")
                }
            )
            val body = response.bodyAsText()
            val decoded = Json.decodeFromString<AdminUserResponse>(body)
            decoded.interactions ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
