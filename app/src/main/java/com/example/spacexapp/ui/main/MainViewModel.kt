package com.example.spacexapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.spacexapp.data.LaunchRepository
import com.example.spacexapp.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    launchRepository: LaunchRepository
) : ViewModel() {
    val launchesDataFlow = launchRepository.getLaunchesDataFlow().cachedIn(viewModelScope)



    private val logger = Logger("MainViewModel")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}
