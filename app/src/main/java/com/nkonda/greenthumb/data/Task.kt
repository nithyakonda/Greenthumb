package com.nkonda.greenthumb.data

import androidx.room.*
import com.nkonda.greenthumb.util.ScheduleConverter

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
    var schedule: Schedule,
    var completed: Boolean = false,
    @ColumnInfo(name = "last_executed") var lastExecuted: Long = System.currentTimeMillis(),
    @ColumnInfo (name = "custom_type") val customType: String = "",
)

data class TaskKey(
    @ColumnInfo (name = "plant_id") val plantId: Long,
    @ColumnInfo(name = "task_type") val taskType: TaskType
)

data class TaskWithPlant(
    @Embedded val task: Task,
    @ColumnInfo(name = "common_name") val plantName: String,
    @ColumnInfo(name = "image") val plantImage: String
)