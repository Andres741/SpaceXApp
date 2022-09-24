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
    private val launchRepository: LaunchRepository
) : ViewModel() {

    private val connexionFlow = networkStatusFlowFactory.new
    private val _loadingStatus = MutableStateFlow(LoadDetailStatus.Loading as LoadDetailStatus)

    private val isPossibleLoad = isPossibleTryLoadFlow(connexionFlow, _loadingStatus)
    val loadingStatus: StateFlow<LoadDetailStatus> = _loadingStatus


    lateinit var launch: LaunchQuery.Launch
        private set

    fun loadData(launchId: String) {
        val tryLoadJob = viewModelScope.launch {
            withContext(Dispatchers.Default) {

                tryLoad(launchId)

                isPossibleLoad.filter { it }.collectLatest {
                    tryLoad(launchId)
                }
            }
        }
        viewModelScope.launch {
            loadingStatus.takeWhile { (it !is LoadDetailStatus.Loaded).ifFalse {
                tryLoadJob.cancel()
            } }.collectLatest {  }
        }
    }

    fun setLoading() {
        _loadingStatus.value = LoadDetailStatus.Loading
    }

    private suspend fun tryLoad(launchId: String) {
        if (_loadingStatus.value is LoadDetailStatus.Loaded) return
        _loadingStatus.value = LoadDetailStatus.Loading

        val launchResult = launchRepository.getLaunch(launchId.log("launchId"))

        launchResult.onSuccess {
            it.launch?.id.log("id")
            it.launch?.mission_name.log("mission_name")

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
