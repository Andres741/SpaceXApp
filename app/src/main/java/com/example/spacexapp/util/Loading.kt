package com.example.spacexapp.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

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
