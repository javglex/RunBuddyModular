package com.skymonkey.core.domain

import java.time.DayOfWeek
import java.time.LocalDateTime

object DateUtil {
    fun getStartOfWeek(): LocalDateTime {
        val now = LocalDateTime.now()
        val startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay()
        return startOfWeek
    }

    fun getEndOfWeek(): LocalDateTime {
        val now = LocalDateTime.now()
        val endOfWeek = now.with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59, 999999999)
        return endOfWeek
    }
}
