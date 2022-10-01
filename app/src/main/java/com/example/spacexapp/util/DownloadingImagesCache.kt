package com.example.spacexapp.util

import android.graphics.drawable.Drawable
import coil.size.Size
import com.example.spacexapp.data.ImageDownloader
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue


/**
 * A image cache that is aware of the loading status and retries to load an image when the internet
 * connexion is restored.
 */
class DownloadingImagesCache (
    connexionFlow: Flow<NetworkStatus>,
    private val imageDownloader: ImageDownloader,
    parent: Job? = null,
    val maxCacheSize: Int = 32,
) {
    private val cacheValuePool = CacheValuePool()

    private val cache = ConcurrentHashMap<String, CacheValue>(maxCacheSize)
    private val queue = ConcurrentLinkedQueue<String>()

    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob(parent))

    private val connexionStateFlow = connexionFlow.stateIn(
        coroutineScope, SharingStarted.WhileSubscribed(), NetworkStatus.Available
    )

    fun getImageFlow(imageURL: String, imageSize: Size? = null): StateFlow<CacheLoadImageStatus> {
        val drawableFromCache = cache[imageURL]
        if (drawableFromCache != null) return drawableFromCache.flow

        return createNewCacheValue(imageURL, imageSize).flow
    }

    private fun createNewCacheValue(imageURL: String, imageSize: Size? = null): CacheValue {
        val cacheValue = cacheValuePool.getValue { flow ->
            coroutineScope.launch {
                val drawable = load(
                    connexionStateFlow,
                    onLoading = { flow.value = CacheLoadImageStatus.Loading },
                    onError = { flow.value = CacheLoadImageStatus.Error(it) }
                ) {
                    imageDownloader.getImage(imageURL, imageSize).mapCatching { it ?: throw IOException("Load image failed") }
                }
                flow.value = CacheLoadImageStatus.Loaded(drawable)
            }
        }
        addToCache(imageURL, cacheValue)
        return cacheValue
    }

    private fun addToCache(imageURL: String, cacheValue: CacheValue) {
        cache[imageURL] = cacheValue
        queue.add(imageURL)
        fixCacheSize()
    }

    private val fixCacheSizeMutex = Mutex()
    private fun fixCacheSize() {
        if (fixCacheSizeMutex.isLocked) return

        coroutineScope.launch {
            fixCacheSizeMutex.withLock {
                while (cache.size > maxCacheSize) {
                    val removed = queue.poll() ?: break
                    removeFromCache(removed)
                }
            }
        }
    }

    fun remove(imageURL: String): Boolean {
        removeFromCache(imageURL)
        return queue.remove(imageURL)
    }

    fun removeIfDownloading(imageURL: String): Boolean {
        val cacheValue = cache[imageURL] ?: return false
        if (cacheValue.flow.value is CacheLoadImageStatus.Loaded) return false

        removeFromCache(imageURL)
        return queue.remove(imageURL)
    }

    fun clear() {
        cache.keys().toList().forEach(::removeFromCache)
        cache.clear()
        queue.clear()
    }

    private fun removeFromCache(imageURL: String) {
        val removed = cache.remove(imageURL) ?: return
        cacheValuePool.putValue(removed)
    }
}

sealed interface CacheLoadImageStatus: LoadStatus {
    @JvmInline
    value class Loaded(val drawable: Drawable): CacheLoadImageStatus, LoadStatus.Loaded
    @JvmInline
    value class Error(val exception: Throwable): CacheLoadImageStatus, LoadStatus.Error
    object Loading: CacheLoadImageStatus, LoadStatus.Loading

    fun isLoadingOrNotInternetException() = this is CacheLoadImageStatus.Loading || (this is Error) && exception !is InternetConnectionLostException
    fun isInternetException() = (this as? Error)?.exception is InternetConnectionLostException
}

fun CacheLoadImageStatus.getDrawableOrNull() = (this as? CacheLoadImageStatus.Loaded)?.drawable
fun CacheLoadImageStatus.getErrorOrNull() = (this as? CacheLoadImageStatus.Error)?.exception

private data class CacheValue (
    var job: Job,
    val flow: MutableStateFlow<CacheLoadImageStatus> = MutableStateFlow(CacheLoadImageStatus.Loading),
) {
    companion object {
        inline fun build(newJobProvider: (MutableStateFlow<CacheLoadImageStatus>) -> Job): CacheValue {
            val flow = MutableStateFlow(CacheLoadImageStatus.Loading as CacheLoadImageStatus)
            val job = newJobProvider(flow)
            return CacheValue(job, flow)
        }
    }
}

private class CacheValuePool {
    private val queue = ConcurrentLinkedQueue<CacheValue>() // A stack is faster, but there are not a concurrent stack

    private fun generateNewValue(job: Job) = CacheValue(job)

    fun getValue(job: Job): CacheValue = queue.poll()?.also { it.job = job } ?: generateNewValue(job)

    fun getValue(newJobProvider: (MutableStateFlow<CacheLoadImageStatus>) -> Job): CacheValue {
        val value = queue.poll() ?: return CacheValue.build(newJobProvider)
        value.job = newJobProvider(value.flow)
        return value
    }


    fun putValue(cacheValue: CacheValue) {
        cacheValue.job.cancel()
        cacheValue.flow.value = CacheLoadImageStatus.Loading
        queue.add(cacheValue)
    }
}
