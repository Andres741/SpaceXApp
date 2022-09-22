package com.example.spacexapp.ui.datail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.spacexapp.databinding.FragmentDetailBinding
import com.example.spacexapp.util.extensions.collectOnUI
import com.example.spacexapp.util.extensions.setTextOrGone
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchDetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LaunchDetailViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDetailBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = LaunchDetailFragmentArgs.fromBundle(arguments!!)
        viewModel.setUp(args.launchId)

        viewModel.observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun LaunchDetailViewModel.observe() {
        missionNameFlow.collectOnUI(viewLifecycleOwner, binding.missionName::setText)
        missionDetailsFlow.collectOnUI(viewLifecycleOwner, binding.description::setTextOrGone)
    }
}
