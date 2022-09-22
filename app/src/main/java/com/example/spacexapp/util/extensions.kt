package com.example.spacexapp.util

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.request.ErrorResult
import coil.request.ImageResult
import coil.request.SuccessResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.system.measureTimeMillis

fun TextView.setTextOrGone(newText: String?) {
    if (newText.isNullOrBlank()) {
        visibility = View.GONE
        text = ""
        return
    }
    visibility = View.VISIBLE
    text = newText
}

inline val Fragment.viewLifecycle get() = viewLifecycleOwner.lifecycle
inline val Fragment.viewCoroutineScope get() = viewLifecycle.coroutineScope


inline var<T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.list: List<T>
    get() = currentList
    set(value) {
        submitList(value)
    }

private val formatter = SimpleDateFormat("EEE, dd/MM/yyyy") // EEE, dd/MM/yyyy - HH:mm:ss
fun Long.formatDate(): String = formatter.format(this)

fun<T: Any> List<T?>?.makeNotNull(): List<T> = this?.filterNotNull() ?: emptyList()

fun<T: Any> List<T?>?.makeNullIfEmpty(): List<T>? = this?.filterNotNull()?.takeIf(List<*>::isNotEmpty)

inline fun <B: Boolean?> B.ifTrue(block: () -> Unit) = apply {
    if (this == true) block()
}
inline fun <B: Boolean?> B.ifFalse(block: () -> Unit) = apply {
    if (this == false) block()
}

fun <T> Flow<T>.collectOnUI(lifecycle: Lifecycle, action: suspend (value: T) -> Unit): Job =
    lifecycle.coroutineScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectLatest(action)
        }
    }

fun <T> Flow<T>.collectOnUI(owner: LifecycleOwner, action: suspend (value: T) -> Unit): Job =
    collectOnUI(owner.lifecycle, action)

inline fun <T> ImageResult.fold(onSuccess: (SuccessResult) -> T, onFailure: (ErrorResult) -> T) =
    when (this) {
        is SuccessResult -> onSuccess(this)
        is ErrorResult -> onFailure(this)
    }

suspend inline fun<T> delayAtLeast(millis: Long, block: () -> T): T {
    val elem: T
    measureTimeMillis {
        elem = block()
    }.also {
        delay(millis - it)
    }
    return elem
}

inline fun<T : Any> createDiffUtil(
    crossinline areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
    crossinline areContentsTheSame: (oldItem: T, newItem: T) -> Boolean
) = object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = areItemsTheSame(oldItem, newItem)
    override fun areContentsTheSame(oldItem: T, newItem: T) = areContentsTheSame(oldItem, newItem)
}
