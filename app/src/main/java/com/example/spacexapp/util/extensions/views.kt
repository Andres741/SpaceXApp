package com.example.spacexapp.util.extensions

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


fun TextView.setTextOrGone(newText: String?) {
    if (newText.isNullOrBlank()) {
        visibility = View.GONE
        text = newText
        return
    }
    visibility = View.VISIBLE
    text = newText
}

fun TextView.setTextOrGone(@StringRes newText: Int?) {
    if (newText == null) {
        visibility = View.GONE
        text = ""
        return
    }
    visibility = View.VISIBLE
    setText(newText)
}

fun TextView.setTextOption(option: Boolean?, @StringRes firstOpt: Int, @StringRes secondOpt: Int) {
    val selected = when(option){
        true -> firstOpt
        false -> secondOpt
        null -> null
    }
    setTextOrGone(selected)
}

fun View.putWebLink(URL: String) {
    setOnClickListener {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URL))
        startActivity(it.context, browserIntent, null)
    }
}

fun View.putWebLinkOrGone(URL: String?) {
    if (URL == null) {
        isVisible = false
        return
    }
    isVisible = true
    putWebLink(URL)
}

inline val Fragment.viewLifecycle get() = viewLifecycleOwner.lifecycle
inline val Fragment.viewCoroutineScope get() = viewLifecycleOwner.lifecycleScope


inline var<T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.list: List<T>
    get() = currentList
    set(value) {
        submitList(value)
    }

inline fun<T : Any> createDiffUtil(
    crossinline areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
    crossinline areContentsTheSame: (oldItem: T, newItem: T) -> Boolean
) = object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = areItemsTheSame(oldItem, newItem)
    override fun areContentsTheSame(oldItem: T, newItem: T) = areContentsTheSame(oldItem, newItem)
}
