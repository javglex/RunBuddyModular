package com.skymonkey.core.domain.run

import java.time.DayOfWeek

data class Weekday(
    val dayOfWeek: DayOfWeek,
    val completedRun: Boolean
)
