package com.example.spacexapp.ui.recycle.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.spacexapp.databinding.ImageItemBinding

class ImageViewHolder private constructor(
    private val binding: ImageItemBinding,
): RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup) = ImageViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )
    }
    fun bind(imageUrl: String) {
        binding.image.load(imageUrl) //can't load all images -> solved?
//        Glide.with(binding.image).load(imageUrl).transform().into(binding.image) // Has a bug in shaping
    }
}
