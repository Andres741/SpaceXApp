package com.example.spacexapp.util

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
    networkStatusFlow.collectLatest {
        if (it.isAvailable) {
            do {
                val loadStatus = loader()
            } while (loadStatus is LoadStatus.Error)
        }
    }
}

fun shouldBeLoadingFlow(networkStatusFlow: Flow<NetworkStatus>, loadStatusFlow: Flow<LoadStatus>) =
    combine(networkStatusFlow, loadStatusFlow) { connexion, load ->
        connexion.isAvailable && load !is LoadStatus.Loaded
    }.distinctUntilChanged()
