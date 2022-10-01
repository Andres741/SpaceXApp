package com.example.spacexapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import okio.IOException
import java.net.InetSocketAddress
import java.net.Socket


sealed class NetworkStatus {

    abstract val isAvailable: Boolean

    val isNotAvailable get() = ! isAvailable

    object Available : NetworkStatus() {
        override val isAvailable = true
    }
    object Unavailable : NetworkStatus() {
        override val isAvailable = false
    }
}

fun getNetworkStatusFlow(context: Context): Flow<NetworkStatus> = callbackFlow {

    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    var noNetJob: Job? = CoroutineScope(Dispatchers.IO).launch {
        delay(100)
        send(NetworkStatus.Unavailable.log())
    }

    val connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            noNetJob?.apply { noNetJob = null; cancel() }
            trySendBlocking(NetworkStatus.Available.log())
        }

//        override fun onUnavailable() {  // works very bad
//            trySendBlocking(NetworkStatus.Unavailable)
//        }

        override fun onLost(network: Network) {
            val isAvailable = connectivityManager.activeNetworkInfo?.isConnected == true
            trySendBlocking(
                if (isAvailable) NetworkStatus.Available.log()
                else NetworkStatus.Unavailable.log()
            )
        }
    }

    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    connectivityManager.registerNetworkCallback(networkRequest, connectivityManagerCallback)
    "new subscription".log()
//    connectivityManager.requestNetwork(networkRequest, connectivityManagerCallback, 100)

    awaitClose{
        noNetJob?.cancel()
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
        "subscription removed".log()
    }

}.distinctUntilChanged()

class NetworkStatusFlowFactory(private val context: Context) {
    val new get() = getNetworkStatusFlow(context)
}

object InternetConnectionLostException: IOException() {
    override val message: String = "Internet connexion lost"
}

private val logger = Logger("Internet")
private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
