package com.example.spacexapp.ui.recycle.viewHolder

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Dimension
import coil.size.Size
import com.example.spacexapp.LaunchQuery
import com.example.spacexapp.databinding.ShipItemBinding
import com.example.spacexapp.util.CacheLoadImageStatus
import com.example.spacexapp.util.DownloadingImagesCache
import com.example.spacexapp.util.OneScopeAtOnceProvider
import com.example.spacexapp.util.extensions.setTextOrGone
import com.example.spacexapp.util.getDrawableOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShipViewHolder private constructor(
    private val binding: ShipItemBinding,
    private val downloadingImagesCache: DownloadingImagesCache,
): RecyclerView.ViewHolder(binding.root) {

    private var ship: LaunchQuery.Ship? = null

    companion object {
        fun create(parent: ViewGroup, shipViewHolderArgs: ShipViewHolderArgs) = ShipViewHolder(
            ShipItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
            shipViewHolderArgs.downloadingImagesCache,
        ).apply {
            val onClick = shipViewHolderArgs.onClickShipItem
            binding.root.setOnClickListener {
                ship?.run(onClick)
            }
        }
    }

    private val scopeFactory = OneScopeAtOnceProvider()
    private val coroutineScope by scopeFactory::currentScope

    private val size = Size (
        height = Dimension(binding.shipImageFrame.layoutParams.height),
        width = Dimension.Undefined
    )


    fun bind(ship: LaunchQuery.Ship) {
        this.ship = ship
        scopeFactory.newScope

        binding.apply {
            shipName.setTextOrGone(ship.name?.trim())

            val imageUrl = ship.image ?: return
            coroutineScope?.launch {
                val imageFlow = withContext(Dispatchers.Default) {
                    downloadingImagesCache.getImageFlow(imageUrl, size)
                }

                imageFlow.collectLatest { loadStatus ->
                    loadingText.isVisible = loadStatus.isLoadingOrNotInternetException()
                    error.isVisible = loadStatus.isInternetException()

                    loadStatus.getDrawableOrNull()?.also { drawable ->
                        shipImage.isVisible = true
                        shipImage.load(drawable)
                    }
                }
            }
        }
    }
}

typealias OnClickShipItem = (LaunchQuery.Ship) -> Unit

data class ShipViewHolderArgs(
    val downloadingImagesCache: DownloadingImagesCache,
    val onClickShipItem: OnClickShipItem,
)

private fun<T> T.log(msj: Any? = null) = apply {
    Log.d("NestedShipViewHolder", "${if (msj != null) "$msj: " else ""}${toString()}")
}
