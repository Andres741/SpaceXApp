package com.example.spacexapp.data

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spacexapp.LaunchesQuery
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class LaunchRepositoryTest {

    private val repo = LaunchRepository(buildApollo())

    @Test
    fun getPage(): Unit= runBlocking {
        "\nGet page test".log()
        val perPage = 5
        val pages = 5
        val totalItems = perPage * pages

        val listActual: List<LaunchesQuery.Launch?>

        measureTimeMillis {
            listActual = repo.getPage(totalItems, 0).data?.launches ?: emptyList()
        }.log("Time get $totalItems")

        val listExpected = ArrayList<LaunchesQuery.Launch?>(totalItems)

        measureTimeMillis {
            repeat(pages) {
                repo.getPage(perPage, it).data?.launches?.also(listExpected::addAll)
            }
        }.log("Time get $pages*$perPage")

        assertEquals(listExpected.size, listActual.size)
        listExpected.forEachIndexed { index, launchExpected ->
            val launchActual = listActual[index]
            assertEquals(launchExpected, launchActual)
        }
    }
}

private fun<T> T.log(msj: Any? = null) = apply {
    Log.d("LaunchRepositoryTest", "${if (msj != null) "$msj: " else ""}${toString()}")
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

private fun<T> T.bigLog(msj: Any? = null) {
    "".log(); toString().uppercase().log(msj); "".log()
}
