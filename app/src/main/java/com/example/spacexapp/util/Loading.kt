package com.example.spacexapp.util

import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*

interface LoadStatus {
    interface Loaded: LoadStatus
    interface Loading: LoadStatus
    interface Error: LoadStatus
}

fun isPossibleTryLoadFlow(networkStatusFlow: Flow<NetworkStatus>, loadStatusFlow: Flow<LoadStatus>) =
    combine(networkStatusFlow, loadStatusFlow) { connexion, load ->
        connexion.isAvailable && load is LoadStatus.Error
    }

// Not tested
suspend fun load(networkStatusFlow: Flow<NetworkStatus>, loader: suspend () -> LoadStatus) {
    coroutineScope {
        networkStatusFlow.filter { it.isAvailable }.collectLatest {
            if (it.isAvailable) {
                do {
                    val loadStatus = loader()
                    if (loadStatus is LoadStatus.Loaded) cancel()
                } while (loadStatus is LoadStatus.Error)
            }
        }
    }
}

fun shouldBeLoadingFlow(networkStatusFlow: Flow<NetworkStatus>, loadStatusFlow: Flow<LoadStatus>) =
    combine(networkStatusFlow, loadStatusFlow) { connexion, load ->
        connexion.isAvailable && load !is LoadStatus.Loaded
    }.distinctUntilChanged()

suspend fun Flow<LoadStatus>.awaitLoadFinish() {
    takeWhile { it !is LoadStatus.Loaded }.lastOrNull()
}
