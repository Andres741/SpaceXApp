package com.example.spacexapp.util

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import java.text.SimpleDateFormat

fun TextView.setTextOrGone(newText: String?) {
    if (newText.isNullOrBlank()) {
        visibility = View.GONE
        text = ""
        return
    }
    visibility = View.VISIBLE
    text = newText
}

private val formatter = SimpleDateFormat("EEE, dd/MM/yyyy") // EEE, dd/MM/yyyy - HH:mm:ss
fun Long.formatDate(): String = formatter.format(this)

fun<T: Any> List<T?>?.makeNotNull(): List<T> = this?.filterNotNull() ?: emptyList()

fun<T: Any> List<T?>?.makeNullIfEmpty(): List<T>? = this?.filterNotNull()?.takeIf(List<*>::isNotEmpty)

inline fun<T : Any> createDiffUtil(
    crossinline areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
    crossinline areContentsTheSame: (oldItem: T, newItem: T) -> Boolean
) = object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = areItemsTheSame(oldItem, newItem)
    override fun areContentsTheSame(oldItem: T, newItem: T) = areContentsTheSame(oldItem, newItem)
}
