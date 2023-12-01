package com.nkonda.greenthumb.data.source

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