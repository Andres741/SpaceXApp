package com.example.spacexapp.ui.recycle.viewHolder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexapp.LaunchesQuery
import coil.load
import com.example.spacexapp.databinding.ShipItemBinding
import com.example.spacexapp.util.setTextOrGone


class ShipViewHolder private constructor(
    private val binding: ShipItemBinding,
): RecyclerView.ViewHolder(binding.root) {

    private var ship: LaunchesQuery.Ship? = null

    companion object {
        fun create(parent: ViewGroup, onClickLaunch: OnClickShipItem) = ShipViewHolder(
            ShipItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        ).apply {
            binding.root.setOnClickListener {
                ship?.run(onClickLaunch)
            }
        }
    }

    fun bind(ship: LaunchesQuery.Ship) {
        this.ship = ship
        binding.apply {
            shipImage.load(ship.image)
            shipName.setTextOrGone(ship.name?.trim())
//            shipName.text = "cont: ${++cont}" // There are recycle between multiple adapters!!
        }
    }
}

typealias OnClickShipItem = (LaunchesQuery.Ship) -> Unit


private fun<T> T.log(msj: Any? = null) = apply {
    Log.d("NestedShipViewHolder", "${if (msj != null) "$msj: " else ""}${toString()}")
}
