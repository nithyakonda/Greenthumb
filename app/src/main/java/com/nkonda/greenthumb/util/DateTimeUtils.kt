package com.nkonda.greenthumb.util

import com.nkonda.greenthumb.data.Day
import com.nkonda.greenthumb.data.Month
import java.util.Calendar

private val cal = Calendar.getInstance()

fun getCurrentDay(): Day {
    return when(cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> Day.Sunday
        Calendar.MONDAY -> Day.Monday
        Calendar.TUESDAY -> Day.Tuesday
        Calendar.WEDNESDAY -> Day.Wednesday
        Calendar.THURSDAY -> Day.Thursday
        Calendar.FRIDAY -> Day.Friday
        Calendar.SATURDAY -> Day.Saturday
        else -> Day.NOT_SET
    }
}

fun getCurrentMonth(): Month {
    return when(cal.get(Calendar.MONTH)) {
        Calendar.JANUARY -> Month.January
        Calendar.FEBRUARY -> Month.February
        Calendar.MARCH -> Month.March
        Calendar.APRIL -> Month.April
        Calendar.MAY -> Month.May
        Calendar.JUNE -> Month.June
        Calendar.JULY -> Month.July
        Calendar.AUGUST -> Month.August
        Calendar.SEPTEMBER -> Month.September
        Calendar.OCTOBER -> Month.October
        Calendar.NOVEMBER -> Month.November
        Calendar.DECEMBER -> Month.December
        else -> Month.NOT_SET
    }
}