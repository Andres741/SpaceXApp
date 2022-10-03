package com.example.spacexapp.util

import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import okio.IOException


interface LoadStatus {
    interface Loaded: LoadStatus
    interface Loading: LoadStatus
    interface Error: LoadStatus
}

fun isPossibleTryLoadFlow(networkStatusFlow: Flow<NetworkStatus>, loadStatusFlow: Flow<LoadStatus>) =
    combine(networkStatusFlow, loadStatusFlow) { connexion, load ->
        connexion.isAvailable && load is LoadStatus.Error
    }


fun shouldBeLoadingFlow(networkStatusFlow: Flow<NetworkStatus>, loadStatusFlow: Flow<LoadStatus>) =
    combine(networkStatusFlow, loadStatusFlow) { connexion, load ->
        connexion.isAvailable && load !is LoadStatus.Loaded
    }.distinctUntilChanged()

suspend fun Flow<LoadStatus>.awaitLoadFinish() {
    takeWhile { it !is LoadStatus.Loaded }.lastOrNull()
}

/**
 * This function will be trying to load until successful stopping when internet connection is not
 * available and restarting when connection restored.
 */
suspend inline fun <T: Any> load(
    networkStatusFlow: Flow<NetworkStatus>,
    crossinline onLoading: () -> Unit,
    crossinline onError: (Throwable) -> Unit,
    crossinline loader: suspend () -> Result<T>,
): T {
    loader().onSuccess { return it }

    var res: T? = null
    supervisorScope {
        launch {
            networkStatusFlow.collectLatest { net ->
                if (net.isNotAvailable) {
                    onError(InternetConnectionLostException)
                    return@collectLatest
                }

                do {
                    onLoading()
                    val result = loader()

                    val isFailure = result.fold(
                        onSuccess = { loaded ->
                            res = loaded
                            cancel()
                            false
                        },
                        onFailure = { t ->
                            onError(t)
                            delay(10)
                            true
                        }
                    )
                } while (isFailure)
            }
        }
    }
    return res!!
}


private val logger = Logger("Loading")
private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
