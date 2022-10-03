package com.example.spacexapp.ui.image

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.example.spacexapp.R
import com.example.spacexapp.databinding.FragmentImageBinding
import com.example.spacexapp.util.*
import com.example.spacexapp.util.extensions.collectOnUI
import com.example.spacexapp.util.extensions.viewCoroutineScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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
        inflater.inflate(R.menu.save_image, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_image -> {
                viewCoroutineScope.launch {
                    @StringRes
                    val msj: Int = kotlin.run {
                        if (viewModel.loadImageStatusFlow.value !is LoadImageStatus.Loaded) return@run R.string.image_not_available
                        return@run if (viewModel.saveImage()) R.string.image_saved else R.string.save_image_error
                    }
                    Toast.makeText(context, msj, Toast.LENGTH_SHORT).show()
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private val logger = Logger("ImageFragment")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}
