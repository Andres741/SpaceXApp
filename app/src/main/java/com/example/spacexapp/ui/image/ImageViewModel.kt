package com.example.spacexapp.ui.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexapp.util.*
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

    private val connexionFlow = networkStatusFlowFactory.new.shareIn(loadScope, SharingStarted.WhileSubscribed())
    val loadImageStatusFlow = MutableStateFlow<LoadImageStatus>(LoadImageStatus.Loading)

    val haveToRetry = isPossibleLoadFlow(connexionFlow, loadImageStatusFlow)

    init {
        viewModelScope.launch {
            loadImageStatusFlow.takeWhile {
                (it is LoadImageStatus.Loaded).ifTrue(loadScope::cancel)
            }
        }
    }

    private val logger = Logger("ImageViewModel")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}

sealed class LoadImageStatus: LoadStatus {
    object Loaded: LoadImageStatus(), LoadStatus.Loaded
    object Loading: LoadImageStatus(), LoadStatus.Loading
    object Error: LoadImageStatus(), LoadStatus.Error
}
