package com.example.spacexapp.ui.recycle.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.spacexapp.LaunchQuery
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.ui.recycle.viewHolder.ImageViewHolderArgs
import com.example.spacexapp.ui.recycle.viewHolder.LaunchViewHolder
import com.example.spacexapp.ui.recycle.viewHolder.OnClickImageViewHolder
import com.example.spacexapp.ui.recycle.viewHolder.OnClickLaunch
import com.example.spacexapp.util.extensions.createDiffUtil


class LaunchesAdapter(
    private val imageViewHolderArgs: ImageViewHolderArgs,
    private val onClickLaunch: OnClickLaunch,
) : PagingDataAdapter<LaunchesQuery.Launch, LaunchViewHolder>(diffUtil) {

    private companion object {
        val diffUtil = createDiffUtil(
            areItemsTheSame = { old, new -> old.mission_name == new.mission_name },
            areContentsTheSame = LaunchesQuery.Launch::equals
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LaunchViewHolder.create(parent, imageViewHolderArgs, onClickLaunch)

    override fun onBindViewHolder(holder: LaunchViewHolder, position: Int) {
        getItem(position)?.also(holder::bind)
    }

    override fun onViewRecycled(holder: LaunchViewHolder) {
        super.onViewRecycled(holder)
        holder.recycle()
    }
}
