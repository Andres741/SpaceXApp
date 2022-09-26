package com.example.spacexapp.util.extensions

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.*
import coil.request.ErrorResult
import coil.request.ImageResult
import coil.request.SuccessResult
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import kotlin.system.measureTimeMillis


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

fun saveImageToStorage(imageName: String? = null, context: Context, bitmap: Bitmap): Boolean {
    val name = imageName ?: "spacex_image.jpg"

    val imageOutStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri: Uri =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false

        context.contentResolver.openOutputStream(uri) ?: return false
    } else {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()

        val image = File(imagesDir, name)
        FileOutputStream(image)
    }

    return imageOutStream.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutStream)
    }
}
