package com.example.spacexapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.spacexapp.LaunchesQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LaunchRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun getAll() = apolloClient.query(LaunchesQuery()).execute()

    suspend fun getPage(perPage: Int, page: Int) = apolloClient.query(LaunchesQuery(
        limit = Optional.present(perPage),
        offset = Optional.present(perPage * page),
    )).execute()

    fun getLaunchesDataFlow(): Flow<PagingData<LaunchesQuery.Launch>> = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
        pagingSourceFactory = { LaunchesPagingSource(::getPage) }
    ).flow

    companion object {
        private const val NETWORK_PAGE_SIZE = 15
    }
}
