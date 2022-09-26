package com.example.spacexapp.data

import android.content.Context
import coil.imageLoader
import coil.request.ImageRequest

class ImageDownloader(private val context: Context) {

    private val imageLoader = context.imageLoader

    suspend fun getImage(imageURL: String) = kotlin.runCatching {
        val request = ImageRequest.Builder(context)
            .data(imageURL)
            .build()

        imageLoader.execute(request).drawable
    }
}
