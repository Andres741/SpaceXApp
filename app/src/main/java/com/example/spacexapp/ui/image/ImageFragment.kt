package com.example.spacexapp.ui.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.example.spacexapp.databinding.FragmentImageBinding

class ImageFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentImageBinding.inflate(inflater, container, false).apply {

        val args = ImageFragmentArgs.fromBundle(arguments!!)
        image.load(args.imageURL)
    }.root
}
