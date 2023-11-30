package com.nkonda.greenthumb.data.source.remote

import com.nkonda.greenthumb.data.Plant
import com.squareup.moshi.Json

data class SearchResult(val data: List<PlantSummary>)

data class PlantSummary(
    val id: Long,
    @Json(name = "common_name") val commonName: String?,
    @Json(name = "scientific_name") val scientificNames: List<String>?,
    val cycle: String?,
    @Json(name = "default_image") val defaultImage: DefaultImage?,
    ) {
    val scientificName: String
        get() = scientificNames.orEmpty().getOrElse(0) {"Unknown"}
}

data class PlantDetails(
    val id: Long,
    @Json(name = "common_name") val commonName: String,
    @Json(name = "scientific_name") val scientificName: String,
    val cycle: String,
    @Json(name = "care_level") val careLevel: Int,
    val sunlight: List<Int>,
    val watering: Int,
    @Json(name = "pruning_month") val pruningMonth: List<String>,
    @Json(name = "interval") val pruningInterval: String, // enum
    val thumbnail: String,
    @Json(name = "original_url") val image: String,
    val description: String,
)

data class DefaultImage(
    @Json(name = "original_url") val originalUrl: String?,
    @Json(name = "regular_url") val regularUrl: String?,
    @Json(name = "medium_url") val mediumUrl: String?,
    @Json(name = "small_url") val smallUrl: String?,
    val thumbnail: String?
)


fun PlantDetails.asDomainModel(): Plant {
    return Plant(this.id,
        this.commonName,
        this.scientificName,
        this.cycle,
        this.careLevel,
        this.sunlight,
        this.watering,
        this.pruningMonth,
        this.pruningInterval,
        this.thumbnail,
        this.image,
        this.description,
        null
    )
}