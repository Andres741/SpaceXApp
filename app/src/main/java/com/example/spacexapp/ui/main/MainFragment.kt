package com.example.spacexapp.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.recyclerview.widget.ConcatAdapter
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.databinding.FragmentMainBinding
import com.example.spacexapp.ui.recycle.adapter.LaunchesAdapter
import com.example.spacexapp.ui.recycle.adapter.LocationLoadingStateAdapter
import com.example.spacexapp.ui.recycle.viewHolder.ImageViewHolderArgs
import com.example.spacexapp.util.*
import com.example.spacexapp.util.extensions.collectOnUI
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private val launchesAdapter by lazy { LaunchesAdapter(
        ImageViewHolderArgs(viewModel.downloadingImagesCache, ::navigateToImage), ::navigateLaunchDetail
    ) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainBinding.inflate(inflater, container, false).apply {
        _binding = this
        launchesRV.adapter = launchesAdapter.setUpAdapter()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun LaunchesAdapter.setUpAdapter(): ConcatAdapter {
        observeAdapter()
        return concatAdapter()
    }

    private fun LaunchesAdapter.observeAdapter() {
        loadStateFlow.collectOnUI(viewLifecycleOwner) {
            viewModel.loadPageState.value = it.source.toLoadPageStatus()

            val refreshStatus = it.source.refresh
            binding.error.isVisible = refreshStatus is LoadState.Error
            binding.progressBar.isVisible = refreshStatus is LoadState.Loading
        }
    }

    private fun LaunchesAdapter.concatAdapter(): ConcatAdapter =
        withLoadStateHeaderAndFooter(
            header = LocationLoadingStateAdapter(),
            footer = LocationLoadingStateAdapter(),
        )

    private fun MainViewModel.observeViewModel() {

        launchesDataFlow.collectOnUI(viewLifecycleOwner, launchesAdapter::submitData)

        connexionFlow.collectOnUI(viewLifecycleOwner) {
            it.isAvailable.log("  internet".uppercase())
        }

        haveToRetry.collectOnUI(viewLifecycleOwner) {
            if (it.not()) {
                "not retry".log()
                return@collectOnUI
            }
            "retiring".log()
            launchesAdapter.retry()
        }
    }

    private fun navigateLaunchDetail(launch: LaunchesQuery.Launch) {
        Log.d("MainFragment", "clicked launch $launch")
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToLoadingLaunchDetailsFragment(launch.mission_name ?: "")
        )
    }

    private fun navigateToImage(imageURL: String) {
        "clicked image $imageURL".log()
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToImageFragment(imageURL)
        )
    }

    private fun LoadStates.toLoadPageStatus(): LoadPageStatus {

        var loadPageStatus = LoadPageStatus.Loaded as LoadPageStatus
        forEach { _, loadState ->
            if (loadState is LoadState.Loading) {
                loadPageStatus = LoadPageStatus.Loading
            }
            if (loadState is LoadState.Error) {
                loadPageStatus = LoadPageStatus.Error
                return@forEach
            }
        }
        return loadPageStatus
    }

    private val logger = Logger("MainFragment")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
    private fun<T, IT: Iterable<T>> IT.logList(msj: Any? = null): IT = logger.logList(this, msj)
    private fun<T, IT: Collection<T>> IT.logListSize(msj: Any? = null): IT = logger.logListSize(this, msj)
    private fun<T> T.bigLog(msj: Any? = null): T = logger.bigLog(this, msj)
}
