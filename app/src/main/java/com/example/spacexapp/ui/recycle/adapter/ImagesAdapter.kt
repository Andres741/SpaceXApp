package com.example.spacexapp.ui.recycle.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexapp.ui.recycle.viewHolder.ImageViewHolder

class ImagesAdapter: RecyclerView.Adapter<ImageViewHolder>() {

    var list: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageViewHolder.create(parent)

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
