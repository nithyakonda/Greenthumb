package com.nkonda.greenthumb.util

import androidx.room.TypeConverter
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.source.remote.PruningCount
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type
import java.util.*

class SunlightListConverter {

    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromString(value: String): List<Sunlight> {
        val listType = Types.newParameterizedType(List::class.java, Sunlight::class.java)
        val adapter: JsonAdapter<List<Sunlight>> = moshi.adapter(listType)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toString(value: List<Sunlight>): String {
        val listType = Types.newParameterizedType(List::class.java, Sunlight::class.java)
        val adapter: JsonAdapter<List<Sunlight>> = moshi.adapter(listType)
        return adapter.toJson(value)
    }
}

class PruningConverter {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val adapter: JsonAdapter<Plant.Pruning> = moshi.adapter(Plant.Pruning::class.java)

    @TypeConverter
    fun fromString(value: String): Plant.Pruning? {
        return try {
            adapter.fromJson(value)
        } catch (e: IOException) {
            null
        }
    }

    @TypeConverter
    fun toString(value: Plant.Pruning?): String {
        return adapter.toJson(value)
    }
}

class MonthListConverter {

    private val moshi = Moshi.Builder().build()

    @TypeConverter
    fun fromString(value: String): List<Month> {
        val listType = Types.newParameterizedType(List::class.java, Month::class.java)
        val adapter: JsonAdapter<List<Month>> = moshi.adapter(listType)
        return adapter.fromJson(value) ?: emptyList()
    }

    @TypeConverter
    fun toString(value: List<Month>): String {
        val listType = Types.newParameterizedType(List::class.java, Month::class.java)
        val adapter: JsonAdapter<List<Month>> = moshi.adapter(listType)
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

fun convertStringListToMonthList(months: List<String>?): List<Month> {
    return months?.map { monthString ->
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
    } ?: listOf()
}

fun getCareLevelEnumFrom(carelevel: String?): CareLevel {
    return carelevel?.let {
        if (carelevel.equals("low", true)) CareLevel.Low
        else if (carelevel.equals("medium", true)) CareLevel.Medium
        else if (carelevel.equals("high", true)) CareLevel.High
        else CareLevel.Unknown
    } ?: CareLevel.Unknown
}

fun getSunlightEnumListFrom(strList: List<String>?): List<Sunlight> {
    return  strList?.let {
        strList.map {
            if(it.equals("full shade", true)) Sunlight.FullShade
            else if(it.equals("part shade", true)) Sunlight.PartShade
            else if(it.equals("sun part shade", true)) Sunlight.SunPartShade
            else if( it.equals("full sun", true)) Sunlight.FullSun
            else Sunlight.Unknown
        }
    } ?: listOf()
}

fun getWateringEnumFrom(watering: String?): Watering {
    Timber.d("Watering level from network = $watering")
    return watering?.let {
        if (watering.equals("frequent", true)) Watering.Frequent
        else if (watering.equals("average", true)) Watering.Average
        else if (watering.equals("minimum", true)) Watering.Minimum
        else if (watering.equals("none", true)) Watering.None
        else Watering.Unknown
    } ?: Watering.Unknown
}

fun getPruningFrom(pruningCount: PruningCount?, pruningMonth: List<String>?): Plant.Pruning {
    return Plant.Pruning(
        convertStringListToMonthList(pruningMonth),
        pruningCount?.amount ?: 0,
        pruningCount?.interval?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            ?: ""
    )
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