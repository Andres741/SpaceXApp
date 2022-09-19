package com.example.spacexapp.util

import com.example.spacexapp.LaunchesQuery

val LaunchesQuery.Launch.timeMillis get() = launch_date_unix?.toString()?.toLongOrNull()?.let { it * 1000 }

val LaunchesQuery.Launch.timeFormatted get() = timeMillis?.formatDate()
