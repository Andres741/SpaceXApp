package com.example.spacexapp.ui.recycle.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.data.timeFormatted
import com.example.spacexapp.databinding.LaunchItemBinding
import com.example.spacexapp.ui.recycle.adapter.ImagesAdapter
import com.example.spacexapp.util.*

class LaunchViewHolder private constructor(
    private val binding: LaunchItemBinding,
    private val imagesAdapter: ImagesAdapter,
): RecyclerView.ViewHolder(binding.root) {

    private var launch: LaunchesQuery.Launch? = null

    companion object {
        fun create(parent: ViewGroup, onClickImage: OnClickImageViewHolder, onClickLaunch: OnClickLaunch): LaunchViewHolder {

            val adapter = ImagesAdapter(onClickImage)

            return LaunchViewHolder(
                LaunchItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                adapter
            ).apply {
                binding.root.setOnClickListener {
                    launch?.run(onClickLaunch)
                }
                binding.shipsRv.adapter = adapter
            }
        }
    }

    fun bind(launch: LaunchesQuery.Launch) {
        this.launch = launch
        binding.apply {
            missionName.text = launch.mission_name
            rocketName.setTextOrGone(launch.rocket?.rocket?.name)
            launchDateUtc.setTextOrGone(launch.timeFormatted)
            details.setTextOrGone(launch.details)

            launch.links?.flickr_images.makeNullIfEmpty()?.also { imageURLs ->
                images.visibility = View.VISIBLE
                shipsRv.visibility = View.VISIBLE
                imagesAdapter.list = imageURLs
            } ?: kotlin.run {
                images.visibility = View.GONE
                shipsRv.visibility = View.GONE
                imagesAdapter.list = emptyList()
            }
        }
    }

    fun recycle() {
        imagesAdapter.list = emptyList()
    }
}

typealias OnClickLaunch = (LaunchesQuery.Launch) -> Unit
