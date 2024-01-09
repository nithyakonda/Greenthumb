package com.nkonda.greenthumb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nkonda.greenthumb.util.*
import java.util.*

@Entity(tableName = "plants")
data class Plant constructor(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "common_name") var commonName: String,
    @ColumnInfo(name = "scientific_name") val scientificName: String,
    val cycle: String, // Enum perennial, annual, biennial, biannual
    @ColumnInfo(name = "care_level") val careLevel: CareLevel,
    @TypeConverters(SunlightListConverter::class)
    val sunlight: List<Sunlight>,
    val watering: Watering,
    @TypeConverters(PruningConverter::class,MonthListConverter::class)
    val pruning: Pruning,
    val thumbnail: String,
    val image: String,
    val description: String,
) {
    fun getExpectedSchedule(taskType: TaskType):Schedule {
        // todo implement
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR)
        val min = cal.get(Calendar.MINUTE)
        return when(taskType) {
            TaskType.PRUNE -> Schedule(
                null,
                pruning.months,
                hour,
                min,
                getExpectedOccurrence(pruning.months)
            )
            TaskType.WATER -> Schedule(
                convertIntListToDayList(listOf(cal.get(Calendar.DAY_OF_WEEK))),
                null, hour, min, getExpectedOccurrence(watering)
            )
            TaskType.CUSTOM -> TODO()
        }
    }

    fun getSunlightText(): String {
        return buildString {
            for((i, v) in sunlight.withIndex()) {
                append(v)
                if (i < sunlight.size - 1) {
                    append("\n")
                }
            }
        }
    }

    private fun getExpectedOccurrence(pruningMonth: List<Month>): TaskOccurrence {
        return TaskOccurrence.YEARLY
        TODO("Not yet implemented")
    }

    private fun getExpectedOccurrence(watering: Watering): TaskOccurrence {
        return TaskOccurrence.WEEKLY
        TODO("Not yet implemented")
    }

    data class Pruning(
        val months:List<Month>,
        val amount: Int,
        val interval: String) {
        override fun toString(): String {
            return super.toString()
            // todo
        }
    }
}