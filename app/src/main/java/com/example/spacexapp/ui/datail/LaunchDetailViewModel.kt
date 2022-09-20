package com.example.spacexapp.ui.datail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacexapp.data.LaunchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LaunchDetailViewModel @Inject constructor(
    private val launchRepository: LaunchRepository
) : ViewModel() {

    private lateinit var launchId: String

    private val _missionNameFlow = MutableStateFlow("")
    val missionNameFlow: StateFlow<String> = _missionNameFlow

    private val _missionDetailsFlow = MutableStateFlow<String?>(null)
    val missionDetailsFlow: StateFlow<String?> = _missionDetailsFlow

    fun setUp(launchId: String) {
        this.launchId = launchId

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                try {
                    val launch = launchRepository.getLaunch(launchId).dataAssertNoErrors.launch!!
                    _missionNameFlow.value = launch.mission_name!!
                    _missionDetailsFlow.value = launch.details
                } catch (e: Exception) {

                }
            }
        }
    }
}
