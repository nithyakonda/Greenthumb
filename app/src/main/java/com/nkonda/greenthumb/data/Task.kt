package com.nkonda.greenthumb.data

import androidx.room.*
import com.nkonda.greenthumb.util.ScheduleConverter
import com.squareup.moshi.JsonClass

@Entity( tableName = "tasks",
    primaryKeys = ["plant_id", "task_type"],
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plant_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task constructor(
    @Embedded val key: TaskKey,
    @TypeConverters(ScheduleConverter::class)
    var schedule: Schedule = Schedule(),
    var completed: Boolean = false,
    @ColumnInfo(name = "last_executed") var lastExecuted: Long = System.currentTimeMillis(),
    @ColumnInfo (name = "custom_type") val customType: String = "",
)

data class TaskKey(
    @ColumnInfo (name = "plant_id") val plantId: Long,
    @ColumnInfo(name = "task_type") val taskType: TaskType
)

@JsonClass(generateAdapter = true)
data class Schedule(
    var days: List<Day>? = null,
    var months: List<Month>? = null,
    var hourOfDay: Int = -1,
    var minute: Int = -1,
    var occurrence: TaskOccurrence = TaskOccurrence.ONCE,
) {
    fun isSet(): Boolean {
        return (days != null || months != null) && hourOfDay != -1 && minute != -1
    }
}

data class TaskWithPlant(
    @Embedded val task: Task,
    @ColumnInfo(name = "common_name") val plantName: String
)