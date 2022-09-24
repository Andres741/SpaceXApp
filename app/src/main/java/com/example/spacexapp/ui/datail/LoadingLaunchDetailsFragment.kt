package com.example.spacexapp.ui.datail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.spacexapp.databinding.FragmentLoadingBinding
import com.example.spacexapp.util.Logger
import com.example.spacexapp.util.extensions.collectOnUI
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoadingLaunchDetailsFragment : Fragment() {

    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LaunchDetailViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentLoadingBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = LoadingLaunchDetailsFragmentArgs.fromBundle(arguments!!)

        viewModel.setLoading()

        viewModel.loadingStatus.collectOnUI(viewLifecycleOwner) {

            binding.apply {
                progressBar.isVisible = it is LoadDetailStatus.Loading
                error.isVisible = it is LoadDetailStatus.Error
            }

            if (it is LoadDetailStatus.Loaded) {
                findNavController().navigate(
                    LoadingLaunchDetailsFragmentDirections.actionLoadingLaunchDetailsFragmentToDetailFragment()
                )
            }
        }

        viewModel.loadData(args.launchId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val logger = Logger("MainFragment")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}
