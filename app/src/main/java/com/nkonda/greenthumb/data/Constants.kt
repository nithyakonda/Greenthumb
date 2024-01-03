package com.nkonda.greenthumb.data

enum class TaskType {
    PRUNE,
    WATER,
    CUSTOM
}

enum class TaskOccurrence {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY,
    ONCE; // DEFAULT
}

enum class Day {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY,
    NOT_SET; // DEFAULT
}

enum class Month {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER,
    NOT_SET; // DEFAULT
}

class ErrorCodes {
    companion object {
        const val NOT_FOUND = "2000"
    }
}