package com.nkonda.greenthumb.util

import com.nkonda.greenthumb.data.Day
import com.nkonda.greenthumb.data.Month
import java.time.LocalDate
import java.util.Calendar

private val cal = Calendar.getInstance()

fun getYesterday(): Pair<Day, Month> {
    val yesterday = cal
    yesterday.add(Calendar.DAY_OF_MONTH, -1)

    val day = yesterday.get(Calendar.DAY_OF_WEEK)
    val month = yesterday.get(Calendar.MONTH) + 1

    return Pair(convertIntToDay(day), convertIntToMonth(month))
}

fun getToday(): Pair<Day, Month> {
    val day = cal.get(Calendar.DAY_OF_WEEK)
    val month = cal.get(Calendar.MONTH) + 1

    return Pair(convertIntToDay(day), convertIntToMonth(month))
}

fun getTimeInMillis(hourOfDay: Int, minute: Int): Long {
    return ((hourOfDay * 60 + minute) * 60 * 1000).toLong()
}