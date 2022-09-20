package com.example.spacexapp.ui.image

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.request.ErrorResult
import coil.request.ImageResult
import coil.request.SuccessResult
import com.bumptech.glide.Glide
import com.example.spacexapp.databinding.FragmentImageBinding
import com.example.spacexapp.util.delayAtLeast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class ImageFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentImageBinding.inflate(inflater, container, false).apply {

        val imageURL = ImageFragmentArgs.fromBundle(arguments!!).imageURL
        loadImage(imageURL)

        error.setOnClickListener {
            error.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            loadImage(imageURL)
        }
    }.root

    private fun FragmentImageBinding.loadImage(imageURL: String) {
        val loadDisposable = image.load("$imageURL/")

        lifecycleScope.launchWhenStarted {
            val result: ImageResult = loadDisposable.job.await()
            "Load of $imageURL finished".log()

            when (result) {
                is SuccessResult -> {
                    "Load was successfully".log()
                    progressBar.visibility = View.GONE
                }
                is ErrorResult -> {
                    "Load error".log()
                    delay(300)
                    progressBar.visibility = View.GONE
                    error.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun<T> T.log(msj: Any? = null) = apply {
        Log.d("ImageFragment", "${if (msj != null) "$msj: " else ""}${toString()}")
    }
}
