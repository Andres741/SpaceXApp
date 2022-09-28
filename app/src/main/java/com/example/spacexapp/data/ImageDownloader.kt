package com.example.spacexapp.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageDownloader(private val context: Context) {

    private val imageLoader = context.imageLoader

    suspend fun getImage(imageURL: String) = kotlin.runCatching {
        val request = ImageRequest.Builder(context)
            .data(imageURL)
            .build()

        imageLoader.execute(request).drawable
    }

    suspend fun saveImageToStorage(bitmap: Bitmap, imageName: String? = null): Boolean = withContext(Dispatchers.IO + NonCancellable) {
        val name = imageName ?: defaultImageName

        val imageOutStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri: Uri =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return@withContext false

            context.contentResolver.openOutputStream(uri) ?: return@withContext false
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()

            val image = File(imagesDir, name)
            FileOutputStream(image)
        }

        return@withContext imageOutStream.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutStream)
        }
    }

    suspend fun downloadFromURL(imageURL: String, imageName: String? = null): Boolean =
        getImage(imageURL).mapCatching { saveImageToStorage(it!!.toBitmap(), imageName) }.getOrNull() ?: false

    companion object {
        const val defaultImageName = "spacex_image.jpg"
    }
}
