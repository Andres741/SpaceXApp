package com.example.spacexapp.util

import android.util.Log

class Logger(
    val tag: String
) {
    fun<T> log(data: T, msj: Any? = null) = data.apply {
        Log.d(tag, "${if (msj != null) "$msj: " else ""}${toString()}")
    }

    fun <T, IT: Iterable<T>> logList(data: IT, msj: Any? = null) = data.apply {
        log("$msj:".uppercase(), null)
        this.iterator().hasNext().takeIf { it } ?: kotlin.run {
            log("  Collection is empty")
            return@apply
        }
        forEachIndexed { index, elem ->
            log(elem, index)
        }
    }

    fun <T> bigLog(data: T, msj: Any? = null) = data.apply {
        log(""); log(toString().uppercase(), msj); log("")
    }

//    fun <T> getLog(): logFun<T> = this::log
//    fun <T, IT: Iterable<T>> getLogList(): logFun<IT> = this::logList
//    fun <T> getBigLog(): logFun<T> = this::bigLog
}

typealias logFun<T> = T.(Any?) -> T

