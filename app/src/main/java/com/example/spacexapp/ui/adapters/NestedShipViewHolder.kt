package com.example.spacexapp.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.databinding.NestedShipItemBinding
import coil.load
import com.example.spacexapp.util.setTextOrGone


class NestedShipViewHolder private constructor(
    private val binding: NestedShipItemBinding,
): RecyclerView.ViewHolder(binding.root) {

    private var ship: LaunchesQuery.Ship? = null

    companion object {
        fun create(parent: ViewGroup, onClickLaunch: OnClickNestedShip) = NestedShipViewHolder(
            NestedShipItemBinding.inflate(
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
//            Glide.with(shipImage).load(ship.image).into(shipImage)
            shipName.setTextOrGone(ship.name?.trim())
//            shipName.text = "cont: ${++cont}" // There are recycle between multiple adapters!!
        }
    }
}

typealias OnClickNestedShip = (LaunchesQuery.Ship) -> Unit


private fun<T> T.log(msj: Any? = null) = apply {
    Log.d("NestedShipViewHolder", "${if (msj != null) "$msj: " else ""}${toString()}")
}
