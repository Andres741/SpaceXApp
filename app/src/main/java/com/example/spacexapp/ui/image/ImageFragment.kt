package com.example.spacexapp.ui.image

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.Disposable
import coil.request.ErrorResult
import coil.request.ImageResult
import coil.request.SuccessResult
import com.example.spacexapp.databinding.FragmentImageBinding
import com.example.spacexapp.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.takeWhile

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

        binding.apply {
            error.setOnClickListener {
                error.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                loadImage(imageURL)
            }
        }

        viewModel.loadStatus.collectOnUI(viewLifecycleOwner) {
            when(it) {
                LoadStatus.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.error.visibility = View.GONE
                }
                LoadStatus.Loaded -> {
                    binding.progressBar.visibility = View.GONE
                    binding.error.visibility = View.GONE
                }
                LoadStatus.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.error.visibility = View.VISIBLE
                }
            }
        }

        viewModel.haveToRetry.collectOnUI(viewLifecycleOwner) {
            if (it) binding.loadImage(imageURL)
        }
    }

    private fun FragmentImageBinding.loadImage(imageURL: String) {

        viewModel.loadStatus.value = LoadStatus.Loading

        image.load("$imageURL/") {
            listener(
                onSuccess = { _, _ ->
                    viewModel.loadStatus.value = LoadStatus.Loaded
                },
                onError = { _, _ ->
                    viewModel.loadStatus.value = LoadStatus.Error
                }
            )
        }
    }

    private val logger = Logger("ImageFragment")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}
