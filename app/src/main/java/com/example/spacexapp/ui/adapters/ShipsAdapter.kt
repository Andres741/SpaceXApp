package com.example.spacexapp.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.util.createDiffUtil

class ShipsAdapter(
    private val onClickNestedShip: OnClickNestedShip,
): ListAdapter<LaunchesQuery.Ship, NestedShipViewHolder>(
    createDiffUtil(
        areItemsTheSame = { old, new -> old.id == new.id },
        areContentsTheSame = LaunchesQuery.Ship::equals
    )
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NestedShipViewHolder.create(parent, onClickNestedShip)

    override fun onBindViewHolder(holder: NestedShipViewHolder, position: Int) {
        getItem(position)?.also(holder::bind)
    }
}
