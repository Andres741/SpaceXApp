package com.example.spacexapp.ui.image

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexapp.data.ImageDownloader
import com.example.spacexapp.util.*
import com.example.spacexapp.util.extensions.ifTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ImageViewModel @Inject constructor(
    networkStatusFlowFactory: NetworkStatusFlowFactory,
    private val imageDownloader: ImageDownloader,
): ViewModel() {

    private val connexionFlow = networkStatusFlowFactory.new
    val loadImageStatusFlow = MutableStateFlow<LoadImageStatus>(LoadImageStatus.Loading)

    private val loadingCallbacks = LoadingCallbacks<Drawable?>(
        onLoading = { loadImageStatusFlow.value = LoadImageStatus.Loading },
        onError = { loadImageStatusFlow.value = LoadImageStatus.Error },
    )

    fun loadImage(imageURL: String) {
        viewModelScope.launch {
            val drawable = load(connexionFlow, loadingCallbacks) {
                imageDownloader.getImage("$imageURL/")
            }
            loadImageStatusFlow.value = LoadImageStatus.Loaded(drawable)
        }
    }

    private val logger = Logger("ImageViewModel")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}

sealed class LoadImageStatus: LoadStatus {
    data class Loaded(val drawable: Drawable?): LoadImageStatus(), LoadStatus.Loaded
    object Loading: LoadImageStatus(), LoadStatus.Loading
    object Error: LoadImageStatus(), LoadStatus.Error
}
