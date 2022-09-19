package com.example.spacexapp.data

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import java.util.concurrent.TimeUnit

const val SPACEX_API_URL = "https://api.spacex.land/graphql/"

fun buildApollo(): ApolloClient {

    val interceptor = HttpLoggingInterceptor().apply { level = Level.BASIC }

    val okHttpClient = OkHttpClient.Builder().apply {
        callTimeout(15, TimeUnit.SECONDS)
        readTimeout(15, TimeUnit.SECONDS)
        addInterceptor(interceptor)
    }.build()

    return ApolloClient.Builder().apply {
        serverUrl(SPACEX_API_URL)
        okHttpClient(okHttpClient)
    }.build()
}
