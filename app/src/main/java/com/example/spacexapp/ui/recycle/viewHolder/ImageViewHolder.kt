package com.example.spacexapp.ui.recycle.viewHolder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Dimension
import coil.size.Size
import com.bumptech.glide.Glide
import com.example.spacexapp.databinding.ImageItemBinding
import com.example.spacexapp.util.CacheLoadImageStatus
import com.example.spacexapp.util.DownloadingImagesCache
import com.example.spacexapp.util.OneScopeAtOnceProvider
import com.example.spacexapp.util.getDrawableOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ImageViewHolder private constructor(
    private val binding: ImageItemBinding,
    private val downloadingImagesCache: DownloadingImagesCache,
): RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(
            parent: ViewGroup,
            imageViewHolderArgs: ImageViewHolderArgs,
        ) = ImageViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
            imageViewHolderArgs.downloadingImagesCache,
        ).apply {
            binding.root.setOnClickListener {
                imageUrl?.also(imageViewHolderArgs.onClickImageViewHolder)
            }
        }
    }

    private val scopeFactory = OneScopeAtOnceProvider()
    private val coroutineScope by scopeFactory::currentScope

    private val size = Size (
        height = Dimension(binding.image.layoutParams.height),
        width = Dimension.Undefined
    )

    var imageUrl: String? = null
        private set

    fun bind(imageUrl: String) {
        this.imageUrl = imageUrl
        scopeFactory.newScope

        binding.apply {
            progressBar.isVisible = true
            error.isVisible = false
            image.isVisible = false

            coroutineScope?.launch {
                val imageFlow = withContext(Dispatchers.Default) {
                    downloadingImagesCache.getImageFlow(imageUrl, size)
                }

                imageFlow.collectLatest { loadStatus ->
                    progressBar.isVisible = loadStatus.isLoadingOrNotInternetException()
                    error.isVisible = loadStatus.isInternetException()

                    loadStatus.getDrawableOrNull()?.also { drawable ->
                        image.isVisible = true
                        image.load(drawable)
                    }
                }
            }
        }

//        binding.apply {
//            progressBar.isVisible = true
//            error.isVisible = false
//
//            image.load(imageUrl.log()) {
//                listener(
//                    onSuccess = { _, _ ->
//                        progressBar.isVisible = false
//                        error.isVisible = false
//                    },
//                    onError = { _, _ ->
//                        "Load error".log()
//                        progressBar.isVisible = true
//                        error.isVisible = true
//                    }
//                )
//            }
//        }
//        Glide.with(binding.image).load(imageUrl).into(binding.image) // Has a bug in shaping
    }

    fun recycle() {
        scopeFactory.cancel()
    }

    private fun<T> T.log(msj: Any? = null) = apply {
        Log.d("ImageViewHolder", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

typealias OnClickImageViewHolder = (String) -> Unit

data class ImageViewHolderArgs (
    val downloadingImagesCache: DownloadingImagesCache,
    val onClickImageViewHolder: OnClickImageViewHolder,
)
