package com.nkonda.greenthumb.data.source.testdoubles

import com.nkonda.greenthumb.data.*
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

val plants: HashMap<Long, Plant> = hashMapOf(
    51L to Plant(51, "cName51", "sName51", "annual", "high", listOf("full_sun"), "high", listOf("April", "May"), "thumbnail51", "imageUrl51", "description51"))

val plantOne = Plant(1, "cName1", "sName1", "annual", "high", listOf("full_sun"), "high", listOf("April", "May"), "thumbnail1", "imageUrl1", "description1")
val plantTwo = Plant(2, "cName2", "sName2", "perennial", "high", listOf("full_sun"), "high", listOf("April", "May"), "thumbnail2", "imageUrl2", "description2")

val tasks:HashMap<TaskKey, Task> = hashMapOf(
    TaskKey(51L, TaskType.Water) to Task(TaskKey(51L, TaskType.Water)),
    TaskKey(51L, TaskType.Prune) to Task(TaskKey(51L, TaskType.Prune))
)

val plantOneWateringTaskDefaultSchedule = Task(TaskKey(1, TaskType.Water))
val plantOnePruningTaskDefaultSchedule = Task(TaskKey(1, TaskType.Prune))
//val plantOneWateringTaskExpectedSchedule = plantOneWateringTaskDefaultSchedule.apply {  schedule = wateringOneExpectedSchedule }


val wateringOneExpectedSchedule = Schedule(listOf(Day.Monday, Day.Tuesday), null, 12, 0, TaskOccurrence.ONCE)