package com.example.spacexapp.util

import kotlinx.coroutines.cancel
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

// Not tested
suspend fun<T> load(networkStatusFlow: Flow<NetworkStatus>, loadingCallbacks: LoadingCallbacks<T>, loader: suspend () -> Result<T>): T {
    var res: T? = null
    supervisorScope {
        launch {
            networkStatusFlow.collectLatest { net ->
                if (net.isNotAvailable) {
                    loadingCallbacks.onError(IOException("Internet connexion lost"))
                    return@collectLatest
                }

                do {
                    loadingCallbacks.onLoading()
                    val result = loader()

                    val isFailure = result.fold(
                        onSuccess = { loaded ->
                            res = loaded
                            cancel()
                            false
                        },
                        onFailure = { t ->
                            loadingCallbacks.onError(t)
                            true
                        }
                    )
                } while (isFailure)
            }
        }
    }
    return res!!
}

data class LoadingCallbacks<T>(
    val onLoading: () -> Unit,
    val onError: (Throwable) -> Unit,
)


private val logger = Logger("Loading")
private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
