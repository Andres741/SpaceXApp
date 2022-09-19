package com.example.spacexapp.ui.recycle.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.spacexapp.LaunchQuery
import com.example.spacexapp.ui.recycle.viewHolder.OnClickShipItem
import com.example.spacexapp.ui.recycle.viewHolder.ShipViewHolder
import com.example.spacexapp.util.createDiffUtil

class ShipsAdapter(
    private val onClickNestedShip: OnClickShipItem,
): ListAdapter<LaunchQuery.Ship, ShipViewHolder>(
    createDiffUtil({ old, new -> old.id == new.id }, LaunchQuery.Ship::equals)
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ShipViewHolder.create(parent, onClickNestedShip)

    override fun onBindViewHolder(holder: ShipViewHolder, position: Int) {
        getItem(position)?.also(holder::bind)
    }
}
