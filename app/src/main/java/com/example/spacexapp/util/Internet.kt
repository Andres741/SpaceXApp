package com.example.spacexapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

sealed class NetworkStatus {

    abstract val isInternetAvailable: Boolean

    object Available : NetworkStatus() {
        override val isInternetAvailable = true
    }
    object Unavailable : NetworkStatus() {
        override val isInternetAvailable = false
    }
}

fun getNetworkStatusFlow(context: Context): Flow<NetworkStatus> = callbackFlow {

    val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val connectivityManagerCallback = object : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            trySendBlocking(NetworkStatus.Available)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            trySendBlocking(NetworkStatus.Unavailable)
        }
    }

    val networkRequest = NetworkRequest
        .Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()
    connectivityManager.registerNetworkCallback(networkRequest, connectivityManagerCallback)

    awaitClose{
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }

}.distinctUntilChanged()

class NetworkStatusFlowFactory(private val context: Context) {
    val value get() = getNetworkStatusFlow(context)
}
