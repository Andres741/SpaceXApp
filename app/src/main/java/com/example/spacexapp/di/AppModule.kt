package com.example.spacexapp.di

import android.content.Context
import com.example.spacexapp.data.ImageDownloader
import com.example.spacexapp.data.buildApollo
import com.example.spacexapp.util.NetworkStatusFlowFactory
import com.example.spacexapp.util.getNetworkStatusFlow
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApollo() = buildApollo()

    @Singleton
    @Provides
    fun provideConnexionFlow(@ApplicationContext context: Context) = NetworkStatusFlowFactory(context)

    @Singleton
    @Provides
    fun provideImageDownloader(@ApplicationContext context: Context) = ImageDownloader(context)
}
