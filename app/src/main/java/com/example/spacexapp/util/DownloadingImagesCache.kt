package com.example.spacexapp.util

import android.graphics.drawable.Drawable
import com.example.spacexapp.data.ImageDownloader
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext


/**
 * A image cache that is aware of the loading status and retries to load an image when the internet
 * connexion is restored.
 */
class DownloadingImagesCache (
    private val connexionFlow: Flow<NetworkStatus>,
    private val imageDownloader: ImageDownloader,
    parent: Job? = null,
    val maxCacheSize: Int = 64,
) {
    private val cache = ConcurrentHashMap<String, CacheValue>(maxCacheSize)
    private val queue: Queue<String>? = if (maxCacheSize == Int.MAX_VALUE) null else ConcurrentLinkedQueue()

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob(parent))

    fun getImageFlow(imageURL: String): StateFlow<CacheLoadImageStatus> {
        val drawableFromCache = cache[imageURL]
        if (drawableFromCache != null) return drawableFromCache.flow

        fixCacheSize(imageURL)

        return createNewDrawableFlow(imageURL).also { newValue ->
            cache[imageURL] = newValue
        }.flow
    }

    private fun createNewDrawableFlow(imageURL: String): CacheValue {
        val newDrawableFlow =  MutableStateFlow(CacheLoadImageStatus.Loading as CacheLoadImageStatus)

        val job = coroutineScope.launch {
            val drawable = load(
                connexionFlow,
                onLoading = { newDrawableFlow.value = CacheLoadImageStatus.Loading },
                onError = { newDrawableFlow.value = CacheLoadImageStatus.Error(it) }
            ) {
                imageDownloader.getImage(imageURL).mapCatching { it ?: throw IOException("Load image failed") }
            }
            newDrawableFlow.value = CacheLoadImageStatus.Loaded(drawable)
        }

        return CacheValue(job, newDrawableFlow)
    }

    private fun fixCacheSize(imageURL: String) {
        queue?.apply {
            add(imageURL)
            if (size > maxCacheSize) {
                cache.remove(imageURL)?.also { removed ->
                    removed.job.cancel()
                }
                poll()
            }
        }
    }

    fun clear() {
        cache.forEach { (_, value) ->
            value.job.cancel()
        }
        cache.clear()
        queue?.clear()
    }
}

sealed class CacheLoadImageStatus: LoadStatus {
    data class Loaded(val drawable: Drawable): CacheLoadImageStatus(), LoadStatus.Loaded
    data class Error(val exception: Throwable): CacheLoadImageStatus(), LoadStatus.Error
    object Loading: CacheLoadImageStatus(), LoadStatus.Loading
}

fun CacheLoadImageStatus.getDrawableOrNull() = (this as? CacheLoadImageStatus.Loaded)?.drawable

data class CacheValue (
    val job: Job,
    val flow: MutableStateFlow<CacheLoadImageStatus>,
)
