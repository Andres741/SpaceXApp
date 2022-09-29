package com.example.spacexapp.ui.recycle.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexapp.ui.recycle.viewHolder.ImageViewHolder
import com.example.spacexapp.ui.recycle.viewHolder.ImageViewHolderArgs
import com.example.spacexapp.ui.recycle.viewHolder.OnClickImageViewHolder
import com.example.spacexapp.util.DownloadingImagesCache

class ImagesAdapter(
    private val imageViewHolderArgs: ImageViewHolderArgs,
): RecyclerView.Adapter<ImageViewHolder>() {

    var list: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageViewHolder.create(parent, imageViewHolderArgs)

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun onViewRecycled(holder: ImageViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }
}
