package com.nkonda.greenthumb.data

import androidx.room.*
import com.squareup.moshi.JsonClass
import java.util.*

@Entity( tableName = "tasks" )
data class Task constructor(
    @ColumnInfo (name = "plant_id") val plantId: Long,
    val type: TaskType,
    val start: Long,
    val end: Long,
    var schedule: Schedule,
    val completed: Boolean,
    @ColumnInfo (name = "custom_type") var customType: String = "",
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
) {
    constructor(
        plantId: Long,
        type: TaskType,
        schedule: Schedule
    ) : this(plantId, type, schedule.getStart(), schedule.getEnd(), schedule, false)

    companion object {
        fun getDefaultTask(plantId: Long, taskType: TaskType): Task {
            return Task(
                plantId,
                taskType,
                Schedule(null, null, null, TaskOccurrence.ONCE)
            ) // todo make it better
        }
    }
}

@JsonClass(generateAdapter = true)
data class Schedule (val days: List<Day>?,
                     val months: List<Month>?,
                     val time: String?,
                     val occurrence: TaskOccurrence) {

    fun isSet(): Boolean {
        return (days != null || months != null) && time != null
    }

    fun getStart(): Long {
        // todo implement
        return 1L
    }

    fun getEnd(): Long {
        // todo implement
        return 2L
    }
}