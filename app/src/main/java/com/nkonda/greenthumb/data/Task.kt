package com.nkonda.greenthumb.data

import androidx.room.*
import com.squareup.moshi.JsonClass
import java.util.*

@Entity( tableName = "tasks", primaryKeys = ["plant_id", "task_type"] )
data class Task constructor(
    @Embedded val key: TaskKey,
    var schedule: Schedule,
    var completed: Boolean = false,
    @ColumnInfo(name = "last_executed") var lastExecuted: Long = System.currentTimeMillis(),
    @ColumnInfo (name = "custom_type") val customType: String = "",
) {
    companion object {
        fun getDefaultTask(taskKey: TaskKey): Task {
            return Task(
                taskKey,
                Schedule(null, null, null, TaskOccurrence.ONCE)
            ) // todo make it better
        }
    }
}

data class TaskKey(
    @ColumnInfo (name = "plant_id") val plantId: Long,
    @ColumnInfo(name = "task_type") val taskType: TaskType
)

@JsonClass(generateAdapter = true)
data class Schedule (val days: List<Day>?,
                     val months: List<Month>?,
                     val time: String?,
                     val occurrence: TaskOccurrence) {

    fun isSet(): Boolean {
        return (days != null || months != null) && time != null
    }

}