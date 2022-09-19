package com.example.spacexapp.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spacexapp.LaunchesQuery

class ShipsAdapter(
    private val onClickNestedShip: OnClickNestedShip,
): RecyclerView.Adapter<NestedShipViewHolder>() {

    var list: List<LaunchesQuery.Ship> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NestedShipViewHolder.create(parent, onClickNestedShip)

    override fun onBindViewHolder(holder: NestedShipViewHolder, position: Int) {
        list[position].also(holder::bind)
    }
}
