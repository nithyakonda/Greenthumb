package com.nkonda.greenthumb.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open abstract class Schedule(
    var days: List<Day>? = null,
    var months: List<Month>? = null,
    var hourOfDay: Int = -1,
    var minute: Int = -1,
    var isPm: Boolean,
    var occurrence: TaskOccurrence = TaskOccurrence.ONCE,
) {
    fun isSet(): Boolean {
        return (days != null || months != null) && hourOfDay != -1 && minute != -1
    }

    abstract fun expectedScheduleString():String
    abstract fun actualScheduleString():String

    protected fun StringBuilder.getTimeString() {
        append(hourOfDay)
        append(":${minute}")
        if (hourOfDay < 12) {
            append(if (isPm) "PM" else "AM")
        }
    }
}

class PruningSchedule(
    months: List<Month>,
    hourOfDay: Int = -1,
    minute: Int = -1,
    isPm: Boolean,
    var notified: Boolean = false): Schedule(null, months, hourOfDay, minute, isPm, TaskOccurrence.YEARLY) {
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
    isPm: Boolean,
    private val expected: Watering): Schedule(days, null, hourOfDay, minute, isPm, TaskOccurrence.WEEKLY) {
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