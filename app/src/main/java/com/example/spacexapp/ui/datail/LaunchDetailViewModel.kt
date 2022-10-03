package com.example.spacexapp.ui.datail

import androidx.lifecycle.ViewModel
import com.example.spacexapp.LaunchQuery
import com.example.spacexapp.data.LaunchRepository
import com.example.spacexapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LaunchDetailViewModel @Inject constructor(
    networkStatusFlowFactory: NetworkStatusFlowFactory,
    private val launchRepository: LaunchRepository,
    val downloadingImagesCache: DownloadingImagesCache,
) : ViewModel() {

    private val coroutineScopeBuilder = OneScopeAtOnceProvider()
    private val loadLaunchScope by coroutineScopeBuilder::currentScope

    private val connexionFlow = networkStatusFlowFactory.new

    private val _loadingStatus = MutableStateFlow(LoadDetailStatus.Loading as LoadDetailStatus)

    val loadingStatus: StateFlow<LoadDetailStatus> = _loadingStatus

    fun initViewModel() {
        coroutineScopeBuilder.newScope
        _loadingStatus.value = LoadDetailStatus.Loading
    }

    fun loadData(missionName: String) {
        loadLaunchScope?.launch(Dispatchers.Default) {
            val launch = load(
                connexionFlow,
                onLoading = { _loadingStatus.value = LoadDetailStatus.Loading },
                onError = { _loadingStatus.value = LoadDetailStatus.Error }
            ) {
                launchRepository.getLaunch(missionName).mapCatching { it!! }
            }

            _loadingStatus.value = LoadDetailStatus.Loaded(launch)
        }
    }

    fun stopViewModel() {
        coroutineScopeBuilder.cancel()
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

sealed interface LoadDetailStatus: LoadStatus {
    @JvmInline
    value class Loaded(val launch: LaunchQuery.Launch): LoadDetailStatus, LoadStatus.Loaded
    object Loading: LoadDetailStatus, LoadStatus.Loading
    object Error: LoadDetailStatus, LoadStatus.Error
}
