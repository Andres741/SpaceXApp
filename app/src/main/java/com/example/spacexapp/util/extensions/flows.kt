package com.example.spacexapp.util.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


fun <T> Flow<T>.collectOnUI(lifecycle: Lifecycle, action: suspend (value: T) -> Unit): Job =
    lifecycle.coroutineScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectLatest(action)
        }
    }

fun <T> Flow<T>.collectOnUI(owner: LifecycleOwner, action: suspend (value: T) -> Unit): Job =
    collectOnUI(owner.lifecycle, action)
