package com.example.spacexapp.ui.recycle.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.ui.recycle.viewHolder.LaunchViewHolder
import com.example.spacexapp.ui.recycle.viewHolder.OnClickImageViewHolder
import com.example.spacexapp.ui.recycle.viewHolder.OnClickLaunch
import com.example.spacexapp.util.extensions.createDiffUtil

class LaunchesAdapter(
    private val onClickImage: OnClickImageViewHolder,
    private val onClickLaunch: OnClickLaunch,
) : PagingDataAdapter<LaunchesQuery.Launch, LaunchViewHolder>(
    createDiffUtil(
        areItemsTheSame = { old, new -> old.id == new.id },
        areContentsTheSame = LaunchesQuery.Launch::equals
    )
) {
//    override fun getItemViewType(position: Int): Int {
//        return 0
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LaunchViewHolder.create(parent, onClickImage, onClickLaunch)

    override fun onBindViewHolder(holder: LaunchViewHolder, position: Int) {
        getItem(position)?.also(holder::bind)
    }

    override fun onViewRecycled(holder: LaunchViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }
}
