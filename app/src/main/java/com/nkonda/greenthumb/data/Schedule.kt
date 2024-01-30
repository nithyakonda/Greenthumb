package com.nkonda.greenthumb.data

import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*

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

    // todo handle both 12hr/24hr formats
    protected fun getTimeString(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
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
}
