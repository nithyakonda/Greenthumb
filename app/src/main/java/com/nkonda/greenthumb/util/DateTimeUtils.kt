package com.nkonda.greenthumb.util

import com.nkonda.greenthumb.data.*
import java.text.SimpleDateFormat
import java.util.*


fun getYesterday(): Pair<Day, Month> {
    val yesterday = Calendar.getInstance(TimeZone.getDefault())
    yesterday.add(Calendar.DAY_OF_MONTH, -1)

    val day = yesterday.get(Calendar.DAY_OF_WEEK)
    val month = yesterday.get(Calendar.MONTH) + 1

    return Pair(convertIntToDay(day), convertIntToMonth(month))
}

fun getToday(): Pair<Day, Month> {
    val today = Calendar.getInstance(TimeZone.getDefault())

    val day = today.get(Calendar.DAY_OF_WEEK)
    val month = today.get(Calendar.MONTH) + 1

    return Pair(convertIntToDay(day), convertIntToMonth(month))
}

fun getDelayUntilTaskSchedulerStartTime(): Long {
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    val currentTime = Calendar.getInstance(TimeZone.getDefault())

    // Set the calendar to the next day if the current time is past 2 AM
    if (calendar.get(Calendar.HOUR_OF_DAY) >= 2) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    // Set the time to 2 AM
    calendar.set(Calendar.HOUR_OF_DAY, 2)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis - currentTime.timeInMillis
}

fun getDelayUntil(hourOfDay: Int, minute: Int): Long {
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    val currentTime = Calendar.getInstance(TimeZone.getDefault())

    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis - currentTime.timeInMillis
}

fun getFormattedTimeString(hourOfDay: Int, minute: Int): String {
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
    calendar.set(Calendar.MINUTE, minute)

    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun getCurrentTimeString(): String {
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    return getFormattedTimeString(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
}

fun isLaterToday(hourOfDay: Int, minute: Int): Boolean {
    val givenTime = Calendar.getInstance(TimeZone.getDefault())
    givenTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
    givenTime.set(Calendar.MINUTE, minute)

    val currentTime = Calendar.getInstance(TimeZone.getDefault())

    return currentTime.timeInMillis < givenTime.timeInMillis
}

fun hasThisMonth(months: List<Month>): Boolean {
    val today = Calendar.getInstance(TimeZone.getDefault())
    return months.contains(convertIntToMonth(today.get(Calendar.MONTH)))
}

fun hasThisDay(days: List<Day>): Boolean {
    val today = Calendar.getInstance(TimeZone.getDefault())
    return days.contains(convertIntToDay(today.get(Calendar.DAY_OF_WEEK)))
}