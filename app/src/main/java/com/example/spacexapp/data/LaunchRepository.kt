package com.example.spacexapp.data

import androidx.paging.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.spacexapp.LaunchQuery
import com.example.spacexapp.LaunchesQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LaunchRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {

    suspend fun getLaunch(launchId: String) = kotlin.runCatching {
        apolloClient.query(LaunchQuery(launchId)).execute().dataAssertNoErrors
    }

    suspend fun getAll() = kotlin.runCatching {
        apolloClient.query(LaunchesQuery()).execute().dataAssertNoErrors
    }

    suspend fun getLaunches(limit: Int, offset: Int) = kotlin.runCatching {
        apolloClient.query(
            LaunchesQuery(
                limit = Optional.present(limit),
                offset = Optional.present(offset),
                sort = Optional.present("launch_date_utc"),
                order = Optional.present("desc"),
            )
        ).execute().dataAssertNoErrors
    }

    suspend fun getPage(perPage: Int, page: Int) = getLaunches(perPage, perPage * page)


    fun getLaunchesDataFlow(): Flow<PagingData<LaunchesQuery.Launch>> = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
        pagingSourceFactory = { LaunchesPagingSource(::getLaunches) }
    ).flow

    companion object {
        private const val NETWORK_PAGE_SIZE = 12
    }
}
