package com.example.langbridge.contacts.data.repository

import com.example.langbridge.ApiEndpoints
import com.example.langbridge.AppHttpClient
import com.example.langbridge.contacts.data.models.ContactResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import kotlinx.serialization.json.Json

class ContactRepositoryImpl : ContactRepository {
    private val httpClient = AppHttpClient()

    override suspend fun getContacts(id: String?): ContactResponse {
        return try {
            val response: HttpResponse = httpClient.post(ApiEndpoints.CONTACT_LIST,
                Parameters.build {
                    append("id", id?:"")

                })
            val responseBody = response.bodyAsText()

            // Attempt to parse JSON response
            try {
                Json.decodeFromString<ContactResponse>(responseBody)
            } catch (e: Exception) {
                // If parsing fails, treat the response as a plain text message
                ContactResponse(contacts = emptyList())
            }
        } catch (e: Exception) {
            ContactResponse(contacts = emptyList())
        }
    }
}