package com.example.langbridge.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class InternetConnectivityManager(
    private val context: Context,
    private val networkCallback: NetworkCallback
) {

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    }


    fun isConnected(): Boolean {
        return connectivityManager.isDefaultNetworkActive
    }

    fun registerConnectivityManager() {
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    fun unregisterConnectivityManager() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}