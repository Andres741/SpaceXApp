package com.example.spacexapp.ui.image

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexapp.data.ImageDownloader
import com.example.spacexapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okio.IOException
import javax.inject.Inject


@HiltViewModel
class ImageViewModel @Inject constructor(
    networkStatusFlowFactory: NetworkStatusFlowFactory,
    private val imageDownloader: ImageDownloader,
    downloadingImagesCache: DownloadingImagesCache,
): ViewModel() {

    private val connexionFlow = networkStatusFlowFactory.new

    private val _loadImageStatusFlow = MutableStateFlow<LoadImageStatus>(LoadImageStatus.Loading)
    val loadImageStatusFlow: StateFlow<LoadImageStatus> = _loadImageStatusFlow


    fun loadImage(imageURL: String) {
        viewModelScope.launch {
            val drawable = load(
                connexionFlow,
                onLoading = { _loadImageStatusFlow.value = LoadImageStatus.Loading },
                onError = { _loadImageStatusFlow.value = LoadImageStatus.Error }
            ) {
                imageDownloader.getImage("$imageURL/").mapCatching { it ?: throw IOException("Load image failed") }
            }
            _loadImageStatusFlow.value = LoadImageStatus.Loaded(drawable)
        }
    }

    suspend fun saveImage(): Boolean = withContext(Dispatchers.Default) {
        val image = (loadImageStatusFlow.value as? LoadImageStatus.Loaded)?.drawable ?: return@withContext false
        imageDownloader.saveImageToStorage(image.toBitmap())
    }


    private val logger = Logger("ImageViewModel")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}

sealed class LoadImageStatus: LoadStatus {
    data class Loaded(val drawable: Drawable?): LoadImageStatus(), LoadStatus.Loaded
    object Loading: LoadImageStatus(), LoadStatus.Loading
    object Error: LoadImageStatus(), LoadStatus.Error
}
