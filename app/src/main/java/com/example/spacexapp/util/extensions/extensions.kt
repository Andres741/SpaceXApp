package com.example.spacexapp.util.extensions

import androidx.lifecycle.*
import coil.request.ErrorResult
import coil.request.ImageResult
import coil.request.SuccessResult
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import kotlin.system.measureTimeMillis


private val formatter = SimpleDateFormat("EEE, dd/MM/yyyy") // EEE, dd/MM/yyyy - HH:mm:ss
fun Long.formatDate(): String = formatter.format(this)

fun<T: Any> Iterable<T?>?.makeNotNull(): List<T> = this?.filterNotNull() ?: emptyList()

fun<T: Any> Iterable<T?>?.makeNullIfEmpty(): List<T>? = this?.filterNotNull()?.takeIf(List<*>::isNotEmpty)

fun<T: Any> Sequence<T?>?.makeNotNull(): Sequence<T> = this?.filterNotNull() ?: emptySequence()

fun<T: Any> Sequence<T?>?.makeNullIfEmpty(): Sequence<T>? = this?.filterNotNull()?.takeIf {
    it.iterator().hasNext()
}

inline fun <B: Boolean?> B.ifTrue(block: () -> Unit) = apply {
    if (this == true) block()
}
inline fun <B: Boolean?> B.ifFalse(block: () -> Unit) = apply {
    if (this == false) block()
}


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
