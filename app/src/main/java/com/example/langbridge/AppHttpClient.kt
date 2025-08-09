package com.example.langbridge

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.ContentType.Application
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

var ip = "192.168.43.113"
const val port = "12344"

object ApiEndpoints {
    const val LOGIN = "login/"
    const val ADMIN_LOGIN = "admin_login/"
    const val LOGIN_OAUTH = "login_oauth/"
    const val CONTACT_LIST = "contactlist/"
    const val LOGOUT = "logout/"
    const val MESSAGE_LIST = "messagelist/"
    const val USER_LIST = "userlist/"
    const val ADD_CONVERSATION = "addconversation/"
    const val CHANGE_LANGUAGE = "changelanguage/"
    const val SIGNUP = "signup/"
    const val ADMIN_USER_INTERACTIONS = "admin_user_interactions/"
    const val CHANGE_NAME = "change_name/"
    const val CHANGE_PASSWORD = "change_password/"

}

class AppHttpClient {
    private val baseUrl = "http://$ip:$port/"
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }) // Configure JSON serialization
        }
    }

    suspend fun post(endPoint: String, parameters: Parameters): HttpResponse {
        return client.post("$baseUrl$endPoint") {
            contentType(Application.FormUrlEncoded)
            setBody(FormDataContent(parameters))
        }
    }
}