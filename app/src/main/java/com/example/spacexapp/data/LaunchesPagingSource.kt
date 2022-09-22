package com.example.spacexapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.util.Logger
import com.example.spacexapp.util.extensions.makeNotNull

class LaunchesPagingSource (
    private val launchPageProvider: LaunchPageProvider
): PagingSource<Int, LaunchesQuery.Launch>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LaunchesQuery.Launch> {
        val page = params.key ?: INITIAL_INDEX

        return launchPageProvider(params.loadSize, page).fold({ response ->

            val launchesPage = response.launches

            LoadResult.Page(
                data = launchesPage.makeNotNull().logListSize("page size"),
                prevKey = if (page == INITIAL_INDEX) null else page - 1,
                nextKey = if (launchesPage.isNullOrEmpty()) null else page + 1
            )
        }, { e ->
            LoadResult.Error(e.log("error"))
        })
    }

    override fun getRefreshKey(
        state: PagingState<Int, LaunchesQuery.Launch>
    ): Int? = state.anchorPosition?.let { anchorPosition ->
        state.closestPageToPosition(anchorPosition)?.prevKey
    }

    companion object {
        const val INITIAL_INDEX = 0
    }
}

typealias LaunchPageProvider = suspend (perPage: Int, page: Int) -> Result<LaunchesQuery.Data>

private val logger = Logger("LaunchesPagingSource")
private fun<T> T.log(msj: Any? = null) = logger.log(this, msj)
private fun<T, IT: Iterable<T>> IT.logList(msj: Any? = null): IT = logger.logList(this, msj)
private fun<T, IT: Collection<T>> IT.logListSize(msj: Any? = null): IT = logger.logListSize(this, msj)
private fun<T> T.bigLog(msj: Any? = null): T = logger.bigLog(this, msj)
