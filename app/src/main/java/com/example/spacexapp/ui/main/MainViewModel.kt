package com.example.spacexapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.spacexapp.data.LaunchRepository
import com.example.spacexapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    networkStatusFlowFactory: NetworkStatusFlowFactory,
    launchRepository: LaunchRepository,
    val downloadingImagesCache: DownloadingImagesCache,
) : ViewModel() {

    val launchesDataFlow = launchRepository.getLaunchesDataFlow().cachedIn(viewModelScope)

    val connexionFlow = networkStatusFlowFactory.new.shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    val loadPageState = MutableStateFlow(LoadPageStatus.Loading as LoadPageStatus)

    val haveToRetry = isPossibleTryLoadFlow(connexionFlow, loadPageState)




    private val logger = Logger("MainViewModel")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}

sealed class LoadPageStatus: LoadStatus {
    object Loaded: LoadPageStatus(), LoadStatus.Loaded
    object Loading: LoadPageStatus(), LoadStatus.Loading
    object Error: LoadPageStatus(), LoadStatus.Error
}
