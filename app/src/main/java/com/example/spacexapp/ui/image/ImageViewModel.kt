package com.example.spacexapp.ui.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexapp.util.Logger
import com.example.spacexapp.util.NetworkStatusFlowFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    networkStatusFlowFactory: NetworkStatusFlowFactory
): ViewModel() {

    private val loadScope = CoroutineScope(Dispatchers.IO)

    private val connexionFlow = networkStatusFlowFactory.value.shareIn(loadScope, SharingStarted.WhileSubscribed())
    val loadStatus = MutableStateFlow<LoadStatus>(LoadStatus.Loading)

    val haveToRetry = combine(connexionFlow, loadStatus) { connexion, load ->
        (connexion.isInternetAvailable.log("internet works") && load.log("load status") is LoadStatus.Error).log("haveToRetry")
    }

    init {
        viewModelScope.launch {
            loadStatus.takeWhile {
                (it is LoadStatus.Loaded).also { isLoaded ->
                    if (isLoaded) loadScope.cancel()
                }
            }
        }
    }

    private val logger = Logger("ImageViewModel")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}

sealed class LoadStatus {
    object Loaded: LoadStatus()
    object Loading: LoadStatus()
    object Error: LoadStatus()
}
