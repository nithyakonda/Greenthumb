package com.nkonda.greenthumb.data

import java.util.UUID

data class Plant constructor(
    val id: Long,
    val commonName: String,
    val scientificName: String,
    val cycle: String, // Enum perennial, annual, biennial, biannual
    val careLevel: String,
    val sunlight: List<String>, // full_shade, part_shade, sun-part_shade, full_sun
    val watering: String, // frequent, average, minimum, none
    val pruningMonth: List<String>,
    val thumbnail: String,
    val image: String,
    val description: String,
    val tasks: List<Task>?
)