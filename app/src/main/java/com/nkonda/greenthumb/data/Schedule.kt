package com.nkonda.greenthumb.data

import com.nkonda.greenthumb.util.getFormattedTimeString
import com.nkonda.greenthumb.util.hasThisDay
import com.nkonda.greenthumb.util.hasThisMonth
import com.nkonda.greenthumb.util.isLaterToday
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open abstract class Schedule(
    val taskType: TaskType,
    var days: List<Day>,
    var months: List<Month>,
    var hourOfDay: Int = -1,
    var minute: Int = -1,
    var occurrence: TaskOccurrence = TaskOccurrence.ONCE,
) {
    abstract fun actualScheduleString():String

    abstract fun shouldScheduleNotification(): Boolean

    // todo handle both 12hr/24hr formats
    protected fun getTimeString(): String {
        return getFormattedTimeString(hourOfDay, minute)
    }
}

class PruningSchedule(
    months: List<Month>,
    hourOfDay: Int = -1,
    minute: Int = -1): Schedule(TaskType.PRUNE, emptyList(), months, hourOfDay, minute, TaskOccurrence.YEARLY) {

    override fun actualScheduleString(): String {
        return StringBuilder().apply {
            if (months?.isNotEmpty() == true) {
                append(months?.joinToString())
                append(" every year \n")
                append("You will be notified at ")
                append(getTimeString())
                append(" on the first day of the month(s)")
            } else {
                append("")
            }
        }.toString()
    }

    override fun shouldScheduleNotification(): Boolean {
        return hasThisMonth(months) && isLaterToday(hourOfDay, minute)
    }
}

class WateringSchedule(
    days: List<Day>,
    hourOfDay: Int = -1,
    minute: Int = -1): Schedule(TaskType.WATER, days, emptyList(), hourOfDay, minute, TaskOccurrence.WEEKLY) {

    override fun actualScheduleString(): String {
        return StringBuilder().apply {
            days?.let {
                append(it?.joinToString())
                append(" every week at ")
                append(getTimeString())
            } ?: ""
        }.toString()
    }

    override fun shouldScheduleNotification(): Boolean {
        return hasThisDay(days) && isLaterToday(hourOfDay, minute)
    }
}
