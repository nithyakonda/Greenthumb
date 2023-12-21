package com.nkonda.greenthumb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nkonda.greenthumb.util.convertIntListToDayList
import com.nkonda.greenthumb.util.convertStringListToMonthList
import java.util.*

@Entity(tableName = "plants")
data class Plant constructor(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "common_name") var commonName: String,
    @ColumnInfo(name = "scientific_name") val scientificName: String,
    val cycle: String, // Enum perennial, annual, biennial, biannual
    @ColumnInfo(name = "care_level") val careLevel: String,
    val sunlight: List<String>, // full_shade, part_shade, sun-part_shade, full_sun
    val watering: String, // frequent, average, minimum, none
    @ColumnInfo(name = "pruning_month") val _pruningMonth: List<String>,
    val thumbnail: String,
    val image: String,
    val description: String,
) {
    val pruningMonth: List<Month>
        get() = convertStringListToMonthList(_pruningMonth)
    fun getExpectedSchedule(taskType: TaskType):Schedule {
        // todo implement
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR)
        val min = cal.get(Calendar.MINUTE)
        return when(taskType) {
            TaskType.PRUNE -> Schedule(
                null,
                pruningMonth,
                hour,
                min,
                getExpectedOccurrence(pruningMonth)
            )
            TaskType.WATER -> Schedule(
                convertIntListToDayList(listOf(cal.get(Calendar.DAY_OF_WEEK))),
                null, hour, min, getExpectedOccurrence(watering)
            )
            TaskType.CUSTOM -> TODO()
        }
    }

    private fun getExpectedOccurrence(pruningMonth: List<Month>): TaskOccurrence {
        return TaskOccurrence.YEARLY
        TODO("Not yet implemented")
    }

    private fun getExpectedOccurrence(watering: String): TaskOccurrence {
        return TaskOccurrence.WEEKLY
        TODO("Not yet implemented")
    }
}