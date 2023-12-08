package com.nkonda.greenthumb.data.testdoubles

import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.source.remote.Images
import com.nkonda.greenthumb.data.source.remote.PlantSummary

val plantSummaries: List<PlantSummary> = listOf(
    PlantSummary(1, "findOne", listOf("sName1"), "annual", Images(thumbnail = "url1")),
    PlantSummary(2, "findTwo", listOf("sName2"), "perennial", Images(thumbnail = "url2")),
    PlantSummary(3, "findTwo", listOf("sName3"), "perennial", Images(thumbnail = "url3")),
    PlantSummary(4, "findThree", listOf("sName4"), "annual", Images(thumbnail = "url4")),
    PlantSummary(5, "findThree", listOf("sName5"), "annual", Images(thumbnail = "url5")),
    PlantSummary(6, "findThree", listOf("sName6"), "annual", Images(thumbnail = "url6")),
)

val plants: MutableList<Plant> = mutableListOf(
    Plant(1, "cName1", "sName1", "annual", "high", listOf("full_sun"), "high", listOf("April", "May"), "thumbnail1", "imageUrl1", "description1", null),
)

val plantOne = Plant(1, "cName1", "sName1", "annual", "high", listOf("full_sun"), "high", listOf("April", "May"), "thumbnail1", "imageUrl1", "description1", null)
val plantTwo = Plant(2, "cName2", "sName2", "perennial", "high", listOf("full_sun"), "high", listOf("April", "May"), "thumbnail2", "imageUrl2", "description2", null)
