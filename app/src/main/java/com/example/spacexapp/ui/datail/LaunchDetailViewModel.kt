package com.example.spacexapp.ui.datail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexapp.LaunchQuery
import com.example.spacexapp.data.LaunchRepository
import com.example.spacexapp.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LaunchDetailViewModel @Inject constructor(
    networkStatusFlowFactory: NetworkStatusFlowFactory,
    private val launchRepository: LaunchRepository
) : ViewModel() {

    private lateinit var launchId: String

    private val connexionFlow = networkStatusFlowFactory.new
    private val _loadingStatus = MutableStateFlow(LoadDetailStatus.Loading as LoadDetailStatus)

    private val haveToBeLoading = isPossibleTryLoadFlow(connexionFlow, _loadingStatus)
    val loadingStatus: StateFlow<LoadDetailStatus> = _loadingStatus


    lateinit var launch: LaunchQuery.Launch
        private set

    fun setUp(launchId: String) {
        this.launchId = launchId

        viewModelScope.launch {
            withContext(Dispatchers.Default) {

                tryLoad()

                haveToBeLoading.filter { it }.collect {  // no latest!!!
                    tryLoad()
                }
            }
        }
    }

    private suspend fun tryLoad() {
        _loadingStatus.value = LoadDetailStatus.Loading

        val launchResult = launchRepository.getLaunch(launchId)

        launchResult.onSuccess {
            launch = it.launch!!
            _loadingStatus.value = LoadDetailStatus.Loaded
            return
        }
        _loadingStatus.value = LoadDetailStatus.Error
    }

    private val logger = Logger("LaunchDetailViewModel")
    private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
}

sealed class LoadDetailStatus: LoadStatus {
    object Loaded: LoadDetailStatus(), LoadStatus.Loaded
    object Loading: LoadDetailStatus(), LoadStatus.Loading
    object Error: LoadDetailStatus(), LoadStatus.Error
}
