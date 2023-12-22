package com.nkonda.greenthumb.util

import androidx.room.TypeConverter
import com.nkonda.greenthumb.data.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type
import java.util.*

class StringListConverter {

    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toString(value: List<String>): String {
        val listType = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)
        return adapter.toJson(value)
    }
}

class ScheduleConverter {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @TypeConverter
    fun fromString(value: String?): Schedule? {
        if (value == null) return null

        val type: Type = Types.newParameterizedType(Schedule::class.java,
            Types.newParameterizedType(List::class.java, Day::class.java),
            Types.newParameterizedType(List::class.java, Month::class.java),
            Integer::class.java,
            Integer::class.java,
            TaskOccurrence::class.java)
        val adapter: JsonAdapter<Schedule> = moshi.adapter(type)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun toString(schedule: Schedule?): String? {
        if (schedule == null) return null

        val type: Type = Types.newParameterizedType(Schedule::class.java,
            Types.newParameterizedType(List::class.java, Day::class.java),
            Types.newParameterizedType(List::class.java, Month::class.java),
            Integer::class.java,
            Integer::class.java,
            TaskOccurrence::class.java
        )
        val adapter: JsonAdapter<Schedule> = moshi.adapter(type)
        return adapter.toJson(schedule)
    }
}

fun convertStringListToMonthList(months: List<String>): List<Month> {
    return months.map { monthString ->
        when (monthString.toLowerCase()) {
            "january" -> Month.JANUARY
            "february" -> Month.FEBRUARY
            "march" -> Month.MARCH
            "april" -> Month.APRIL
            "may" -> Month.MAY
            "june" -> Month.JUNE
            "july" -> Month.JULY
            "august" -> Month.AUGUST
            "september" -> Month.SEPTEMBER
            "october" -> Month.OCTOBER
            "november" -> Month.NOVEMBER
            "december" -> Month.DECEMBER
            else -> throw IllegalArgumentException("Invalid month string: $monthString")
        }
    }
}

fun convertIntListToDayList(days: List<Int>): List<Day> {
    return days.map { dayInt ->
        when(dayInt) {
            Calendar.SUNDAY -> Day.SUNDAY
            Calendar.MONDAY -> Day.MONDAY
            Calendar.TUESDAY -> Day.TUESDAY
            Calendar.WEDNESDAY -> Day.WEDNESDAY
            Calendar.THURSDAY -> Day.THURSDAY
            Calendar.FRIDAY -> Day.FRIDAY
            Calendar.SATURDAY -> Day.SATURDAY
            else -> Day.NOT_SET
        }
    }
}