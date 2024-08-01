package com.example.langbridge

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType.Application
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val ip = "192.168.43.113"
const val port = "12344"

object ApiEndpoints {
    const val LOGIN = "login/"
    const val CONTACT_LIST = "contactlist/"
    const val LOGOUT = "logout/"
    const val MESSAGE_LIST = "messagelist/"

}

class AppHttpClient {
    private val baseUrl = "http://$ip:$port/"
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }) // Configure JSON serialization
        }
    }

    suspend fun post(endPoint:String,parameters: Parameters): HttpResponse {
       return client.post("$baseUrl$endPoint") {
            contentType(Application.FormUrlEncoded)
            setBody(FormDataContent(parameters))
        }
    }

}