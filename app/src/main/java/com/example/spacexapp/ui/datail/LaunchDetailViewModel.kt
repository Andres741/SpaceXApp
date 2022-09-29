package com.example.spacexapp.ui.datail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexapp.LaunchQuery
import com.example.spacexapp.data.LaunchRepository
import com.example.spacexapp.util.*
import com.example.spacexapp.util.extensions.ifFalse
import com.example.spacexapp.util.extensions.ifTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LaunchDetailViewModel @Inject constructor(
    networkStatusFlowFactory: NetworkStatusFlowFactory,
    private val launchRepository: LaunchRepository,
    val downloadingImagesCache: DownloadingImagesCache,
) : ViewModel() {

    private val connexionFlow = networkStatusFlowFactory.new
    private val _loadingStatus = MutableStateFlow(LoadDetailStatus.Loading as LoadDetailStatus)

    val loadingStatus: StateFlow<LoadDetailStatus> = _loadingStatus

    lateinit var launch: LaunchQuery.Launch
        private set

    fun setLoading() {
        _loadingStatus.value = LoadDetailStatus.Loading
    }

    fun loadData(launchId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            launch = load(
                connexionFlow,
                onLoading = { _loadingStatus.value = LoadDetailStatus.Loading },
                onError = { _loadingStatus.value = LoadDetailStatus.Error }
            ) {
                launchRepository.getLaunch(launchId)
            }.launch!!

            _loadingStatus.value = LoadDetailStatus.Loaded
        }
    }

//    private val isPossibleLoad = isPossibleTryLoadFlow(connexionFlow, _loadingStatus)
//    fun loadData(launchId: String) {
//        val tryLoadJob = viewModelScope.launch(Dispatchers.Default) {
//
//            tryLoad(launchId)
//
//            isPossibleLoad.filter { it }.collectLatest {
//                tryLoad(launchId)
//            }
//        }
//        viewModelScope.launch(Dispatchers.Default) {
//            loadingStatus.awaitLoadFinish()
//            tryLoadJob.cancel()
//        }
//    }
//    private suspend fun tryLoad(launchId: String) {
//        if (_loadingStatus.value is LoadDetailStatus.Loaded) return
//        _loadingStatus.value = LoadDetailStatus.Loading
//
//        val launchResult = launchRepository.getLaunch(launchId)
//
//        launchResult.onSuccess { data ->
//            launch = data.launch!!
//            _loadingStatus.value = LoadDetailStatus.Loaded
//            return
//        }
//        _loadingStatus.value = LoadDetailStatus.Error
//    }

    private val logger = Logger("LaunchDetailViewModel")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}

sealed class LoadDetailStatus: LoadStatus {
    object Loaded: LoadDetailStatus(), LoadStatus.Loaded
    object Loading: LoadDetailStatus(), LoadStatus.Loading
    object Error: LoadDetailStatus(), LoadStatus.Error
}
