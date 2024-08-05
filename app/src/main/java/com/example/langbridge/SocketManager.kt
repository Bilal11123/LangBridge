package com.example.langbridge

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.example.langbridge.messages.data.models.Message
import com.example.langbridge.messages.data.models.SocketMessage
import com.example.langbridge.utils.InternetConnectivityManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


const val socketIp = "192.168.43.113"
const val socketPort = 12344

class SocketManager(context: Context) {
    private var job: Job? = null
    private var client: HttpClient? = null
    private var webSocketSession: DefaultWebSocketSession? = null
    private var internetConnectivityManager: InternetConnectivityManager? = null
    private val connectionListener = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            connect()
        }
    }
    private var onMessageReceivedCallback: ((Message?) -> Unit)? = null

    init {
        internetConnectivityManager = InternetConnectivityManager(context, connectionListener)
        connect()
    }

    private fun createClient(): HttpClient {
        return HttpClient(CIO) {
            install(WebSockets)
        }
    }

    fun setOnMessageReceivedCallback(callback: (Message?) -> Unit) {
        onMessageReceivedCallback = callback
    }

    fun connect() {
        job = CoroutineScope(Dispatchers.IO).launch {
            disconnect()
            client = createClient()
            try {
                client?.webSocket(
                    method = HttpMethod.Get,
                    host = socketIp,
                    port = socketPort,
                    path = "/ws/${UserInfo.id}"
                ) {
                    webSocketSession = this

                    while (true) {
                        val frame = incoming.receive()
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            val message = parseMessage(text)
                            withContext(Dispatchers.Main) {
                                message?.let { onMessageReceivedCallback?.invoke(it.message) }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*fun startListening(callback: (Message) -> Unit) {
        if (internetConnectivityManager?.isConnected() == false) {
            return
        }

        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val input = BufferedReader(InputStreamReader(socket?.getInputStream()))

                while (true) {
                    val json = input.readLine() ?: break

                    val message = parseMessage(json)

                    withContext(Dispatchers.Main) {
                        message?.let { callback(it) }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/

    suspend fun sendMessage(message: SocketMessage) {
        val json = Json.encodeToString(SocketMessage.serializer(), message)
        webSocketSession?.send(Frame.Text(json))
    }



    private fun parseMessage(json: String): SocketMessage? {
        return try {
            Json.decodeFromString<SocketMessage>(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun disconnect() {
        webSocketSession?.close()
        client?.close()
    }


    suspend fun stopListening() {
        disconnect()
        job?.cancel()
    }


}