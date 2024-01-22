package com.nkonda.greenthumb.data

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

    abstract fun expectedScheduleString():String
    abstract fun actualScheduleString():String

    protected fun StringBuilder.getTimeString() {
        append(hourOfDay)
        append(":${minute}")
    }
}

class PruningSchedule(
    months: List<Month>,
    hourOfDay: Int = -1,
    minute: Int = -1,
    var notified: Boolean? = false): Schedule(TaskType.PRUNE, emptyList(), months, hourOfDay, minute, TaskOccurrence.YEARLY) {
    override fun expectedScheduleString(): String {
        return StringBuilder().apply {
            if (months?.isNotEmpty() == true) {
                append("Prune every year in ")
                append(months?.joinToString(separator = "/"))
            } else {
                append("Turn on to set pruning reminders")
            }
        }.toString()
    }

    override fun actualScheduleString(): String {
        return StringBuilder().apply {
            if (months?.isNotEmpty() == true) {
                append(months?.joinToString())
                append(" every year \n")
                append(" You will be notified at ")
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
    minute: Int = -1,
    private val expected: Watering): Schedule(TaskType.WATER, days, emptyList(), hourOfDay, minute, TaskOccurrence.WEEKLY) {
    override fun expectedScheduleString():String {
        return when(expected) {
            Watering.Frequent -> "Water every day"
            Watering.Average -> "Water every other day"
            Watering.Minimum -> "Water twice a week"
            Watering.None -> "Water once a week"
            Watering.Unknown -> "Turn on to set watering reminders"
        }
    }

    override fun actualScheduleString(): String {
        return StringBuilder().apply {
            days?.let {
                append(it?.joinToString())
                append(" every week at ")
                getTimeString()
            } ?: ""
        }.toString()
    }
}

open class MyBaseClass(val name: String) {
    fun displayInfo() {
        println("Name: $name")
    }
}

class MyDerivedClass(name: String, val age: Int) : MyBaseClass(name) {
    fun displayExtendedInfo() {
        displayInfo()
        println("Age: $age")
    }
}