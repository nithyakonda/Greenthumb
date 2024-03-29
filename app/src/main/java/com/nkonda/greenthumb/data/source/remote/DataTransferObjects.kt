package com.nkonda.greenthumb.data.source.remote

import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.util.*
import com.squareup.moshi.Json

data class SearchResult(val data: List<PlantSummary>)

data class PlantSummary(
    val id: Long,
    @Json(name = "common_name") val _commonName: String?,
    @Json(name = "scientific_name") val scientificNames: List<String>?,
    val cycle: String?,
    @Json(name = "default_image") val defaultImage: Images?,
    ) {
    val commonName: String
        get() = capitalizeEachWord(_commonName)
    val scientificName: String
        get() = scientificNames.orEmpty().getOrElse(0) {"Unknown"}
}

data class PlantDetails(
    val id: Long,
    @Json(name = "common_name") val commonName: String?,
    @Json(name = "scientific_name") val scientificNames: List<String>?,
    val cycle: String?,
    @Json(name = "care_level") val careLevel: String?,
    val sunlight: List<String>?,
    val watering: String?,
    @Json(name = "pruning_month") val pruningMonth: List<String>?,
    @Json(name = "pruning_count") val pruningCount: List<PruningCount>?,
    @Json(name = "default_image") val images: Images?,
    val description: String?,
)

data class PruningCount(
    val amount: Int?,
    val interval: String? // enum
)

data class Images(
    @Json(name = "original_url") val originalUrl: String?,
    @Json(name = "regular_url") val regularUrl: String?,
    @Json(name = "medium_url") val mediumUrl: String?,
    @Json(name = "small_url") val smallUrl: String?,
    val thumbnail: String?
) {
    constructor(thumbnail: String) : this(null, null, null, null, thumbnail)
    constructor(thumbnail: String, originalUrl: String) : this(originalUrl, null, null, null, thumbnail)
}

fun PlantDetails.asDomainModel(): Plant {
    return Plant(this.id,
        capitalizeEachWord(this.commonName),
        this.scientificNames.orEmpty().getOrElse(0) {"Unknown"},
        this.cycle.orEmpty(),
        getCareLevelEnumFrom(this.careLevel),
        getSunlightEnumListFrom(this.sunlight)!!,
        getWateringEnumFrom(this.watering),
        getPruningFrom(this.pruningCount, this.pruningMonth),
        this.images?.thumbnail.orEmpty(),
        this.images?.originalUrl.orEmpty(),
        this.description.orEmpty()
    )
}

fun capitalizeEachWord(input: String?): String {
    return input?.let {
        it.split(" ").joinToString(" ") { it.capitalize() }
    } ?: ""
}