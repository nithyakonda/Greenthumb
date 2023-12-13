package com.nkonda.greenthumb.data

import androidx.room.*
import com.nkonda.greenthumb.util.ScheduleConverter
import com.squareup.moshi.JsonClass
import java.util.*

@Entity( tableName = "tasks" )
data class Task constructor(
    @ColumnInfo (name = "plant_id") val plantId: Long,
    val type: TaskType,
    val start: Long,
    val end: Long,
    val schedule: Schedule,
    val completed: Boolean,
    @ColumnInfo (name = "custom_type") val customType: String = "",
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
) {
    data class Builder(val plantId: Long,
                       val type: TaskType,
                       var schedule: Schedule,
                       var customType: String = "") {
        fun build(): Task {
            return Task(plantId, type, schedule.getStart(), schedule.getEnd(), schedule, false, customType)
        }
    }
}

@JsonClass(generateAdapter = true)
data class Schedule (val days: List<Day>?,
                     val months: List<Month>?,
                     val time: String?,
                     val occurrence: TaskOccurrence) {
    fun getStart(): Long {
        // todo implement
        return 1L
    }

    fun getEnd(): Long {
        // todo implement
        return 2L
    }
}