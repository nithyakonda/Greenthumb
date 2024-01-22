package com.nkonda.greenthumb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.material.timepicker.TimeFormat
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
            TaskType.PRUNE -> PruningSchedule(
                pruning.months,
                hour,
                min
            )
            TaskType.WATER -> WateringSchedule(
                convertIntListToDayList(listOf(cal.get(Calendar.DAY_OF_WEEK))),
                hour,
                min,
                watering
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