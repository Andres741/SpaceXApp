package com.example.spacexapp.util

import android.util.Log

class Logger(
    val tag: String
) {
    fun<T> log(data: T, msj: Any? = null) = data.apply {
        Log.d(tag, "${if (msj != null) "$msj: " else ""}${toString()}")
    }

    fun <T, IT: Iterable<T>> logList(data: IT, msj: Any? = null) = data.apply {
        msj?.also { log(it.toString().uppercase()) }
        this.iterator().hasNext().takeIf { it } ?: kotlin.run {
            log("  Collection is empty")
            return@apply
        }
        forEachIndexed { index, elem ->
            log(elem, index)
        }
    }

    fun <T, C: Collection<T>> logListSize(data: C, msj: Any? = null) = data.apply {
        log(size, msj)
    }

    fun <T> bigLog(data: T, msj: Any? = null) = data.apply {
        log(""); log(toString().uppercase(), msj); log("")
    }

//    fun <T> getLog(): logFun<T> = this::log
//    fun <T, IT: Iterable<T>> getLogList(): logFun<IT> = this::logList
//    fun <T> getBigLog(): logFun<T> = this::bigLog
}

//typealias logFun<T> = T.(Any?) -> T

//private val logger = Logger("Logger")
//private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
//private fun<T, IT: Iterable<T>> IT.logList(msj: Any? = null): IT = logger.logList(this, msj)
//private fun<T, IT: Collection<T>> IT.logListSize(msj: Any? = null): IT = logger.logListSize(this, msj)
//private fun<T> T.bigLog(msj: Any? = null): T = logger.bigLog(this, msj)
