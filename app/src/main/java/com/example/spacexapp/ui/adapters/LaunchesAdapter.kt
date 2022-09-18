package com.example.spacexapp.ui.adapters

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.util.createDiffUtil

class LaunchesAdapter(
    private val onClickLaunch: OnClickLaunch
) : PagingDataAdapter<LaunchesQuery.Launch, LaunchViewHolder>(
    createDiffUtil(
        areItemsTheSame = { old, new -> old.id == new.id },
        areContentsTheSame = LaunchesQuery.Launch::equals
    )
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LaunchViewHolder.create(parent, onClickLaunch)

    override fun onBindViewHolder(holder: LaunchViewHolder, position: Int) {
        getItem(position)?.also(holder::bind)
    }
}

