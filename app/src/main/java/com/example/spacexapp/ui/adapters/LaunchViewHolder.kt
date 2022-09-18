package com.example.spacexapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.databinding.LaunchItemBinding
import com.example.spacexapp.util.formatDate
import com.example.spacexapp.util.setTextOrGone
import com.example.spacexapp.util.toLongOrNull

class LaunchViewHolder private constructor(
    private val binding: LaunchItemBinding,
): RecyclerView.ViewHolder(binding.root) {

    private var launch: LaunchesQuery.Launch? = null

    companion object {
        fun create(parent: ViewGroup, onClickLaunch: OnClickLaunch) = LaunchViewHolder(
            LaunchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        ).apply {
            binding.root.setOnClickListener {
                launch?.run(onClickLaunch)
            }
        }
    }

    fun bind(launch: LaunchesQuery.Launch) {
        this.launch = launch
        binding.apply {
            missionName.text = launch.mission_name
            rocketName.setTextOrGone(launch.rocket?.rocket?.name)
            launchDateUtc.setTextOrGone(launch.launch_date_unix?.toLongOrNull()?.formatDate())
            details.setTextOrGone(launch.details)
        }
    }
}

typealias OnClickLaunch = (LaunchesQuery.Launch) -> Unit
