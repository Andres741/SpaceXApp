package com.example.spacexapp.ui.recycle.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.spacexapp.ui.recycle.viewHolder.NetworkStateItemViewHolder

class LocationLoadingStateAdapter: LoadStateAdapter<NetworkStateItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        NetworkStateItemViewHolder.create(parent)

    override fun onBindViewHolder(holder: NetworkStateItemViewHolder, loadState: LoadState) =
        holder.bind(loadState)
}
