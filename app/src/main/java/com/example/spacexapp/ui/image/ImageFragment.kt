package com.example.spacexapp.ui.image

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.example.spacexapp.R
import com.example.spacexapp.databinding.FragmentImageBinding
import com.example.spacexapp.util.*
import com.example.spacexapp.util.extensions.collectOnUI
import com.example.spacexapp.util.extensions.saveImageToStorage
import dagger.hilt.android.AndroidEntryPoint
import java.io.FileOutputStream


@AndroidEntryPoint
class ImageFragment: Fragment() {

    private lateinit var binding: FragmentImageBinding

    private val viewModel: ImageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentImageBinding.inflate(inflater, container, false).apply {
        setHasOptionsMenu(true)
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageURL = ImageFragmentArgs.fromBundle(arguments!!).imageURL

        viewModel.loadImage(imageURL)
        viewModel.loadImageStatusFlow.collectOnUI(viewLifecycleOwner) { status ->
            binding.progressBar.isVisible = status is LoadImageStatus.Loading
            binding.error.isVisible = status is LoadImageStatus.Error

            (status as? LoadImageStatus.Loaded)?.drawable?.also(binding.image::load)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.download_image, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.download_image -> {
                val image = (viewModel.loadImageStatusFlow.value as? LoadImageStatus.Loaded)?.drawable ?: return true

                saveImageToStorage(context!!, image.toBitmap())
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private val logger = Logger("ImageFragment")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}
