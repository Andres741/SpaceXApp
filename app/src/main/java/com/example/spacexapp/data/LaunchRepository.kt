package com.example.spacexapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
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

    suspend fun getPage(perPage: Int, page: Int) = kotlin.runCatching {
        apolloClient.query(
            LaunchesQuery(
                limit = Optional.present(perPage),
                offset = Optional.present(perPage * page),
                sort = Optional.present("launch_date_utc"),
                order = Optional.present("desc")
            )
        ).execute().dataAssertNoErrors
    }

    fun getLaunchesDataFlow(): Flow<PagingData<LaunchesQuery.Launch>> = Pager(
        config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
        pagingSourceFactory = { LaunchesPagingSource(::getPage) }
    ).flow

    companion object {
        private const val NETWORK_PAGE_SIZE = 12
    }
}
