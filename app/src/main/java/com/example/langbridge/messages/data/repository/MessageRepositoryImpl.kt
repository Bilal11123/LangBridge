package com.example.langbridge.messages.data.repository

import com.example.langbridge.ApiEndpoints
import com.example.langbridge.AppHttpClient
import com.example.langbridge.ip
import com.example.langbridge.messages.data.models.MessageResponse
import com.example.langbridge.port
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application
import io.ktor.http.Parameters
import io.ktor.http.contentType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MessageRepositoryImpl : MessageRepository {
    private val httpClient = AppHttpClient()

    override suspend fun getMessageList(id: String?): MessageResponse {
        return try {

            val response: HttpResponse = httpClient.post(
                ApiEndpoints.MESSAGE_LIST,
                Parameters.build {
                    append("id", id?:"")

                })

            val responseBody = response.bodyAsText()

            // Attempt to parse JSON response
            try {
                Json.decodeFromString<MessageResponse>(responseBody)
            } catch (e: Exception) {
                // If parsing fails, treat the response as a plain text message
                MessageResponse(messages = emptyList())
            }
        } catch (e: Exception) {
            MessageResponse(messages = emptyList())
        }
    }
}