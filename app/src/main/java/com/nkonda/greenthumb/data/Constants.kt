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
    Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday,
    NOT_SET; // DEFAULT
}

enum class Month {
    January, February, March, April, May, June, July, August, September, October, November, December,
    NOT_SET; // DEFAULT
}

enum class CareLevel {
    Low, Medium, High, Unknown
}

enum class Sunlight(val string: String) {
    FullShade("Full sun"),
    PartShade("Part shade"),
    SunPartShade("Sun part shade"),
    FullSun("Full sun"),
    Unknown("Unknown");

    override fun toString(): String {
        return this.string
    }
}

enum class Watering {
    Frequent, Average, Minimum, None, Unknown
}

enum class ErrorCode(val code: String, val message: String) {

    // Errors from mobile device
    NO_INTERNET(
        "1000",
        "Looks like your internet connection is taking a nap in the shade!"
    ),

    // Errors from remote api
    NOT_FOUND("2000", "Well, this is awkward... \\n Plant not found in the digital garden!"),
    TIMEOUT("2001", ""),
    FAILED("2002", ""),

    UNKNOWN_ERROR("9000", "Oops-a-daisy! Something went wrong in our plant-tastic system");


    companion object {
        fun fromCode(code: String): ErrorCode {
            return values().find { it.code == code } ?: UNKNOWN_ERROR
        }
    }
}