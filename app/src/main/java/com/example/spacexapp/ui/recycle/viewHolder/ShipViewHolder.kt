package com.example.spacexapp.ui.recycle.viewHolder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.spacexapp.LaunchQuery
import com.example.spacexapp.databinding.ShipItemBinding
import com.example.spacexapp.util.extensions.setTextOrGone

class ShipViewHolder private constructor(
    private val binding: ShipItemBinding,
): RecyclerView.ViewHolder(binding.root) {

    private var ship: LaunchQuery.Ship? = null

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

    fun bind(ship: LaunchQuery.Ship) {
        this.ship = ship
        binding.apply {
            shipImage.load(ship.image)
            shipName.setTextOrGone(ship.name?.trim())
//            shipName.text = "cont: ${++cont}" // There are recycle between multiple adapters!!
        }
    }
}

typealias OnClickShipItem = (LaunchQuery.Ship) -> Unit


private fun<T> T.log(msj: Any? = null) = apply {
    Log.d("NestedShipViewHolder", "${if (msj != null) "$msj: " else ""}${toString()}")
}
