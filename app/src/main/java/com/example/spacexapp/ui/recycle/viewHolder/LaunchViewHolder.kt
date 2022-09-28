package com.example.spacexapp.ui.recycle.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.R
import com.example.spacexapp.data.timeFormatted
import com.example.spacexapp.databinding.LaunchItemBinding
import com.example.spacexapp.ui.recycle.adapter.ImagesAdapter
import com.example.spacexapp.util.extensions.makeNotNull
import com.example.spacexapp.util.extensions.setTextOption
import com.example.spacexapp.util.extensions.setTextOrGone

class LaunchViewHolder private constructor(
    private val binding: LaunchItemBinding,
    private val imagesAdapter: ImagesAdapter,
): RecyclerView.ViewHolder(binding.root) {

    private var launch: LaunchesQuery.Launch? = null

    private var imagesURLs
        get() = imagesAdapter.list
        set(value) {
            imagesAdapter.list = value
//            value.isNotEmpty().also(binding.images::isVisible.setter).also(binding.imagesRv::isVisible.setter)
            val existsImages = value.isNotEmpty()
            binding.images.isVisible = existsImages
            binding.imagesRv.isVisible = existsImages
        }


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
                binding.imagesRv.adapter = adapter
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
            launchSuccess.setTextOption(launch.launch_success, R.string.successful, R.string.failed)

            imagesURLs = launch.links?.flickr_images.makeNotNull()
        }
    }

    fun recycle() {
        imagesURLs = emptyList()
    }
}

typealias OnClickLaunch = (LaunchesQuery.Launch) -> Unit
