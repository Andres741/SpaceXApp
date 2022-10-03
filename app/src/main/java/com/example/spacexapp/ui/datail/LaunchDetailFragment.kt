package com.example.spacexapp.ui.datail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.spacexapp.LaunchQuery
import com.example.spacexapp.R
import com.example.spacexapp.data.timeFormatted
import com.example.spacexapp.databinding.FragmentDetailBinding
import com.example.spacexapp.ui.recycle.adapter.ImagesAdapter
import com.example.spacexapp.ui.recycle.adapter.ShipsAdapter
import com.example.spacexapp.ui.recycle.viewHolder.ImageViewHolderArgs
import com.example.spacexapp.ui.recycle.viewHolder.ShipViewHolderArgs
import com.example.spacexapp.util.Logger
import com.example.spacexapp.util.extensions.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LaunchDetailFragment : Fragment() {

    private val viewModel: LaunchDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDetailBinding.inflate(inflater, container, false).apply {

        val launch: LaunchQuery.Launch = (viewModel.loadingStatus.value as? LoadDetailStatus.Loaded)?.launch ?: throw IllegalStateException(
            "LaunchDetailViewModel.loadingStatus.value should be LoadDetailStatus.Loaded in LaunchDetailFragment"
        )
        setUpUI(launch)
    }.root

    private fun FragmentDetailBinding.setUpUI(launch: LaunchQuery.Launch) {
        missionName.text = launch.mission_name
        launchSite.setTextOrGone(launch.launch_site?.site_name_long)
        description.setTextOrGone(launch.details)
        launchDateUtc.setTextOrGone(launch.timeFormatted)
        launchSuccess.setTextOption(launch.launch_success, R.string.successful, R.string.failed)

        setUpLinks(launch.links)
        setUpImages(launch.links?.flickr_images)
        setUpRocket(launch.rocket?.rocket)
        setUpShips(launch.ships)
    }

    private fun FragmentDetailBinding.setUpLinks(launchLinks: LaunchQuery.Links?) {
        launchLinks?.apply {
            articleLink.putWebLinkOrGone(article_link)
            videoLink.putWebLinkOrGone(video_link)
        }
        val existsLinks = launchLinks?.article_link != null || launchLinks?.video_link != null
        links.isVisible = existsLinks
    }

    private fun FragmentDetailBinding.setUpImages(flickr_images: List<String?>?) {
        flickr_images.makeNullIfEmpty()?.also { imageURLs ->
            val imageViewHolderArgs = ImageViewHolderArgs(viewModel.downloadingImagesCache, ::navigateToImage)
            images.adapter = ImagesAdapter(imageViewHolderArgs).apply {
                list = imageURLs
            }
        }
    }

    private fun FragmentDetailBinding.setUpRocket(rocket: LaunchQuery.Rocket1?) {
        if (rocket == null) {
            rocketInfo.isVisible = false
            return
        }
        rocketInfo.isVisible = true
        rocketName.text = rocket.name
        rocketDescription.setTextOrGone(rocket.description)
        rocketCompany.setTextOrGone(rocket.company)

        rocketWikiLink.putWebLinkOrGone(rocket.wikipedia)
    }

    private fun FragmentDetailBinding.setUpShips(launchShips: List<LaunchQuery.Ship?>?) {
        launchShips.makeNullIfEmpty()?.also { ships ->
            val shipViewHolderArgs = ShipViewHolderArgs(viewModel.downloadingImagesCache) {
                if (it.image == null) {
                    Toast.makeText(context, R.string.ship_no_image, Toast.LENGTH_SHORT).show()
                    return@ShipViewHolderArgs
                }
                navigateToImage(it.image)
            }

            shipsRv.adapter = ShipsAdapter(shipViewHolderArgs).apply {
                list = ships
            }
        } ?: kotlin.run {
            shipsInfo.isVisible = false
        }
    }

    private fun navigateToImage(imageURL: String) {
        "clicked image $imageURL".log()
        findNavController().navigate(
            LaunchDetailFragmentDirections.actionDetailFragmentToImageFragment(imageURL)
        )
    }

    private val logger = Logger("LaunchDetailFragment")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}
