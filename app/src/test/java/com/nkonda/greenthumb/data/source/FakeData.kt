package com.nkonda.greenthumb.data.source

import com.nkonda.greenthumb.data.source.remote.DefaultImage
import com.nkonda.greenthumb.data.source.remote.PlantSummary

val plantSummaries: List<PlantSummary> = listOf(
    PlantSummary(1, "findOne", listOf("sName1"), "annual", DefaultImage(thumbnail = "url1")),
    PlantSummary(2, "findTwo", listOf("sName2"), "perennial", DefaultImage(thumbnail = "url2")),
    PlantSummary(3, "findTwo", listOf("sName3"), "perennial", DefaultImage(thumbnail = "url3")),
    PlantSummary(4, "findThree", listOf("sName4"), "annual", DefaultImage(thumbnail = "url4")),
    PlantSummary(5, "findThree", listOf("sName5"), "annual", DefaultImage(thumbnail = "url5")),
    PlantSummary(6, "findThree", listOf("sName6"), "annual", DefaultImage(thumbnail = "url6")),
)