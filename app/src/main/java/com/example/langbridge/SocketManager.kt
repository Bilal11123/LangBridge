package com.example.langbridge

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.example.langbridge.utils.InternetConnectivityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

const val socketIp = "192.168.43.113"
const val socketPort = 12345

class SocketManager(private val context: Context) {
    private var job: Job? = null
    private var socket: Socket? = null
    private var internetConnectivityManager: InternetConnectivityManager? = null
    private val connectionListener = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            connect()
        }
    }

    init {
        internetConnectivityManager = InternetConnectivityManager(context, connectionListener)
    }


    fun connect() {
        try {
            disconnect()
            socket = Socket(socketIp, socketPort)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startListening(callback: (String) -> Unit) {
        if (internetConnectivityManager?.isConnected() == false) {
            return
        }

        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val input = BufferedReader(InputStreamReader(socket?.getInputStream()))

                // Continuously listen for messages
                while (true) {
                    val message = input.readLine() ?: break // Exit loop on null message

                    // Update receivedMessage via callback
                    withContext(Dispatchers.Main) {
                        callback(message)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun disconnect() {
        socket?.close()
    }


    fun stopListening() {
        job?.cancel()
    }
}