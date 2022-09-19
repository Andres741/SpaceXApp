package com.example.spacexapp.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.databinding.FragmentMainBinding
import com.example.spacexapp.ui.adapters.LaunchesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private val launchesAdapter by lazy { LaunchesAdapter(::navigateLaunchDetail, ::navigateShipDetail) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainBinding.inflate(inflater, container, false).apply {
        _binding = this
        launchesRV.adapter = launchesAdapter
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun MainViewModel.observeViewModel() {
        lifecycleScope.launch {
            launchesDataFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest(launchesAdapter::submitData)
        }
//        launchesDataFlow.asLiveData().observe(viewLifecycleOwner) {
//            it ?: return@observe
//            lifecycleScope.launch {
//                launchesAdapter.submitData(it)
//            }
//        }
    }

    private fun navigateLaunchDetail(launch: LaunchesQuery.Launch) {
        Log.d("MainFragment", "clicked launch $launch")

        if (false) { // useless but interesting
            val strn: String? = launch.id
            if (strn == null) {
                val nothing: Nothing? = strn
            } else {
                val str: String = strn
            }
//        val nothing: Nothing? = strn
//        val str: String = strn
        }
    }

    private fun navigateShipDetail(ship: LaunchesQuery.Ship) {
        Log.d("MainFragment", "clicked ship: $ship")
    }

}