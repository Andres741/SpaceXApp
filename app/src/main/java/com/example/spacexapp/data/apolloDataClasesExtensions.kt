package com.example.spacexapp.data

import com.example.spacexapp.LaunchesQuery
import com.example.spacexapp.util.formatDate

val LaunchesQuery.Launch.timeMillis get() = launch_date_unix?.toString()?.toLongOrNull()?.let { it * 1000 }

val LaunchesQuery.Launch.timeFormatted get() = timeMillis?.formatDate()
