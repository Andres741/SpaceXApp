package com.example.spacexapp.ui.recycle.viewHolder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.spacexapp.databinding.ImageItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageViewHolder private constructor(
    private val binding: ImageItemBinding,
): RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup, onClickImageViewHolder: OnClickImageViewHolder) = ImageViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        ).apply {
            binding.root.setOnClickListener {
                imageUrl?.also(onClickImageViewHolder)
            }
        }
    }

    var imageUrl: String? = null
        private set

    fun bind(imageUrl: String) {
        this.imageUrl = imageUrl

        binding.image.load(imageUrl.log()) {
            listener(onSuccess = { _, _ ->
                binding.progressBar.visibility = View.GONE
            })
        }

//        Glide.with(binding.image).load(imageUrl).into(binding.image) // Has a bug in shaping
    }

    fun recycle() {
        //loadJob?.cancel()
    }

    private fun<T> T.log(msj: Any? = null) = apply {
        Log.d("ImageViewHolder", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}

typealias OnClickImageViewHolder = (String) -> Unit
