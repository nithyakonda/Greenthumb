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
    val cycle: String,
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
    fun getDefaultSchedule(taskType: TaskType):Schedule {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)
        return when(taskType) {
            TaskType.Prune -> PruningSchedule(
                pruning.months,
                hour,
                min
            )
            TaskType.Water -> WateringSchedule(
                convertIntListToDayList(listOf(cal.get(Calendar.DAY_OF_WEEK))),
                hour,
                min
            )
            TaskType.Custom -> TODO()
        }
    }

    fun getExpectedScheduleString(taskType: TaskType): String {
         return when(taskType) {
             TaskType.Prune -> {
                 StringBuilder().apply {
                     if (pruning.months?.isNotEmpty() == true) {
                         append("Prune every year in ")
                         append(pruning.months?.joinToString(separator = "/"))
                     } else {
                         append("Turn on to set pruning reminders")
                     }
                 }.toString()
             }
             TaskType.Water -> {
                 when(watering) {
                     Watering.Frequent -> "Water every day"
                     Watering.Average -> "Water every other day"
                     Watering.Minimum -> "Water twice a week"
                     Watering.None -> "Water once a week"
                     Watering.Unknown -> "Turn on to set watering reminders"
                 }
             }
             TaskType.Custom -> TODO()
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

    data class Pruning(
        val months:List<Month>,
        val amount: Int = 0,
        val interval: String = "") {
        override fun toString(): String {
            return super.toString()
            // todo
        }
    }
}