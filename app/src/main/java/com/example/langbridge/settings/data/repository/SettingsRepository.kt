package com.example.langbridge.settings.data.repository


import com.example.langbridge.ApiEndpoints
import com.example.langbridge.AppHttpClient
import com.example.langbridge.contacts.data.models.LanguageChangeResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SettingsRepository {
    private val httpClient = AppHttpClient()

    suspend fun changeLanguageServerside(userId: String?, language: String?): LanguageChangeResponse {
        return try {
            val response = httpClient.post(ApiEndpoints.CHANGE_LANGUAGE,
                Parameters.build {
                    append("id", userId ?: "")
                    append("language", language ?: "")
                })
            val responseBody = response.bodyAsText()
            Json.decodeFromString<LanguageChangeResponse>(responseBody)
        } catch (e: Exception) {
            LanguageChangeResponse(status = "Failed")
        }
    }

    suspend fun changeNameServerside(userId: String?, newName: String?): GenericResponse {
        return try {
            val response = httpClient.post(ApiEndpoints.CHANGE_NAME,
                Parameters.build {
                    append("user_id", userId ?: "")
                    append("new_name", newName ?: "")
                })
            val responseBody = response.bodyAsText()
            Json.decodeFromString<GenericResponse>(responseBody)
        } catch (e: Exception) {
            GenericResponse(status = "Failed", message = "Failed to change name")
        }
    }

    suspend fun changePasswordServerside(userId: String?, newPassword: String?): GenericResponse {
        return try {
            val response = httpClient.post(ApiEndpoints.CHANGE_PASSWORD,
                Parameters.build {
                    append("user_id", userId ?: "")
                    append("new_password", newPassword ?: "")
                })
            val responseBody = response.bodyAsText()
            Json.decodeFromString<GenericResponse>(responseBody)
        } catch (e: Exception) {
            GenericResponse(status = "Failed", message = "Failed to change password")
        }
    }
}

@Serializable
data class GenericResponse(
    val status: String,
    val message: String
)

