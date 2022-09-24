package com.example.spacexapp.data

import com.example.spacexapp.LaunchQuery
import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.util.extensions.formatDate

fun Any.unixDateToTimeMillis() = toString().toLongOrNull()?.let { it * 1000 }

val LaunchesQuery.Launch.timeMillis get() = launch_date_unix?.unixDateToTimeMillis()
val LaunchesQuery.Launch.timeFormatted get() = timeMillis?.formatDate()

val LaunchQuery.Launch.timeMillis get() = launch_date_unix?.unixDateToTimeMillis()
val LaunchQuery.Launch.timeFormatted get() = timeMillis?.formatDate()
