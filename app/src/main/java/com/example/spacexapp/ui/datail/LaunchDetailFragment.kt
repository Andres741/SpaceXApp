package com.example.spacexapp.ui.datail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.spacexapp.R
import com.example.spacexapp.data.timeFormatted
import com.example.spacexapp.databinding.FragmentDetailBinding
import com.example.spacexapp.ui.recycle.adapter.ImagesAdapter
import com.example.spacexapp.ui.recycle.adapter.ShipsAdapter
import com.example.spacexapp.ui.recycle.viewHolder.ImageViewHolderArgs
import com.example.spacexapp.util.Logger
import com.example.spacexapp.util.extensions.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LaunchDetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LaunchDetailViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDetailBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun LaunchDetailViewModel.observe() {
        binding.apply {
            missionName.text = launch.mission_name
            launchSite.setTextOrGone(launch.launch_site?.site_name_long)
            description.setTextOrGone(launch.details)
            launchDateUtc.setTextOrGone(launch.timeFormatted)
            launchSuccess.setTextOption(launch.launch_success, R.string.successful, R.string.failed)

            launch.links?.apply {
                articleLink.putWebLinkOrGone(article_link)
                videoLink.putWebLinkOrGone(video_link)
            }

            launch.links?.flickr_images.makeNullIfEmpty()?.also { imageURLs ->
                val imageViewHolderArgs = ImageViewHolderArgs(viewModel.downloadingImagesCache, ::navigateToImage)
                images.adapter = ImagesAdapter(imageViewHolderArgs).apply {
                    list = imageURLs
                }
            }

            launch.rocket?.rocket?.also { rocket ->
                rocketInfo.isVisible = true
                rocketName.text = rocket.name
                rocketDescription.setTextOrGone(rocket.description)
                rocketCompany.setTextOrGone(rocket.company)

                rocketWikiLink.putWebLinkOrGone(rocket.wikipedia)

            } ?: kotlin.run {
                rocketInfo.isVisible = false
            }

            launch.ships.makeNullIfEmpty()?.also { ships ->
                shipsRv.adapter = ShipsAdapter {
                    if (it.image == null) {
                        Toast.makeText(context, R.string.ship_no_image, Toast.LENGTH_SHORT).show()
                        return@ShipsAdapter
                    }
                    navigateToImage(it.image)
                }.apply {
                    list = ships
                }
            } ?: kotlin.run {
                shipsInfo.isVisible = false
            }
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
