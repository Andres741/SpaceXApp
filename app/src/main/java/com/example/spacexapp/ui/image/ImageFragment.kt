package com.example.spacexapp.ui.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.example.spacexapp.databinding.FragmentImageBinding

class ImageFragment: Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentImageBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = ImageFragmentArgs.fromBundle(arguments!!)

        binding.image.load(args.imageURL)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
