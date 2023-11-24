package com.nkonda.greenthumb.data

import java.util.UUID

data class Plant constructor(
    var id: String,
    var commonName: String,
    var scientificName: String,
    var careLevel: Int,
    var sunlight: Int,
    var wateringLevel: Int,
    var pruneTime: List<String>,
    var thumbnailUrl: String,
    var imageUrl: String,
    var description: String,
) {
}