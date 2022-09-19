package com.example.spacexapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.databinding.LaunchItemBinding
import com.example.spacexapp.util.*
import kotlinx.android.synthetic.main.launch_item.view.*

class LaunchViewHolder private constructor(
    private val binding: LaunchItemBinding,
    private val shipsAdapter: ShipsAdapter,
): RecyclerView.ViewHolder(binding.root) {

    private var launch: LaunchesQuery.Launch? = null

    companion object {
        fun create(parent: ViewGroup, onClickLaunch: OnClickLaunch, onClickNestedShip: OnClickNestedShip): LaunchViewHolder {

            val adapter = ShipsAdapter(onClickNestedShip = onClickNestedShip)

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

            launch.ships.makeNullIfEmpty()?.also { shipList ->
                ships.visibility = View.VISIBLE
                shipsRv.visibility = View.VISIBLE
                shipsAdapter.list = shipList  // (shipsRv.adapter as ShipsAdapter).list = shipList
            } ?: kotlin.run {
                binding.ships.visibility = View.GONE
                binding.shipsRv.visibility = View.GONE
                shipsAdapter.list = emptyList()
            }
            launch.ships.makeNotNull()
        }
    }

    fun recycle() {
        shipsAdapter.list = emptyList()
    }
}

typealias OnClickLaunch = (LaunchesQuery.Launch) -> Unit
