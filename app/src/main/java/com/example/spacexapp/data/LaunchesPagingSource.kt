package com.example.spacexapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.util.makeNotNull
import javax.inject.Inject

class LaunchesPagingSource (
    private val launchPageProvider: LaunchPageProvider
): PagingSource<Int, LaunchesQuery.Launch>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LaunchesQuery.Launch> {
        val page = params.key ?: INITIAL_INDEX

        return launchPageProvider(params.loadSize, page).fold({ response ->

            val launchesPage = response.launches

            LoadResult.Page(
                data = launchesPage.makeNotNull(),
                prevKey = if (page == INITIAL_INDEX) null else page - 1,
                nextKey = if (launchesPage.isNullOrEmpty()) null else page + 1
            )
        }, { e ->
            LoadResult.Error(e)
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

private fun<T> T.log(msj: Any? = null) = apply {
    Log.d("LaunchesPagingSource", "${if (msj != null) "$msj: " else ""}${toString()}")
}

private fun<T, IT: Iterable<T>> IT.logList(msj: Any? = null) = apply {
    "$msj:".uppercase().log()
    this.iterator().hasNext().takeIf { it } ?: kotlin.run {
        "  Collection is empty".log()
        return@apply
    }
    forEachIndexed { index, elem ->
        elem.log(index)
    }
}

private fun<T> T.bigLog(msj: Any? = null) = apply {
    "".log(); toString().uppercase().log(msj); "".log()
}
