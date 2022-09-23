package com.example.spacexapp.ui.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.example.spacexapp.databinding.FragmentImageBinding
import com.example.spacexapp.util.*
import com.example.spacexapp.util.extensions.collectOnUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageFragment: Fragment() {

    private lateinit var binding: FragmentImageBinding

    private val viewModel: ImageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentImageBinding.inflate(inflater, container, false).apply {
        binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageURL = ImageFragmentArgs.fromBundle(arguments!!).imageURL

        binding.loadImage(imageURL)

        viewModel.loadImageStatusFlow.collectOnUI(viewLifecycleOwner) {
            binding.apply {
                progressBar.isVisible = it is LoadImageStatus.Loading
                error.isVisible = it is LoadImageStatus.Error
            }
        }

        viewModel.haveToRetry.collectOnUI(viewLifecycleOwner) {
            if (it) binding.loadImage(imageURL)
        }
    }

    private fun FragmentImageBinding.loadImage(imageURL: String) {

        viewModel.loadImageStatusFlow.value = LoadImageStatus.Loading

        image.load("$imageURL/") {
            listener(
                onSuccess = { _, _ ->
                    viewModel.loadImageStatusFlow.value = LoadImageStatus.Loaded
                },
                onError = { _, _ ->
                    viewModel.loadImageStatusFlow.value = LoadImageStatus.Error
                }
            )
        }
    }

    private val logger = Logger("ImageFragment")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}
