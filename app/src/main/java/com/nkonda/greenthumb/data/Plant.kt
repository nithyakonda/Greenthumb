package com.nkonda.greenthumb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "plants")
data class Plant constructor(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "common_name") val commonName: String,
    @ColumnInfo(name = "scientific_name") val scientificName: String,
    val cycle: String, // Enum perennial, annual, biennial, biannual
    @ColumnInfo(name = "care_level") val careLevel: String,
    val sunlight: List<String>, // full_shade, part_shade, sun-part_shade, full_sun
    val watering: String, // frequent, average, minimum, none
    @ColumnInfo(name = "pruning_month") val pruningMonth: List<String>,
    val thumbnail: String,
    val image: String,
    val description: String,
)