package com.nkonda.greenthumb.data.testdoubles

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

const val localPlantOneId = 11L
private val plant11 = Plant(11, "cName11", "sName11", "annual", "high", listOf("full_sun"), "high", listOf("April", "May"), "thumbnail11", "imageUrl11", "description11")
val localPlants: MutableMap<Long, Plant> = mutableMapOf(11L to plant11)

const val remotePlantOneId = 21L
private val plant21 = Plant(21, "cName21", "sName21", "perennial", "high", listOf("full_sun"), "high", listOf("April", "May"), "thumbnail21", "imageUrl21", "description21")
val remotePlants: Map<Long, Plant> = mutableMapOf(21L to plant21)


val tasks:HashMap<TaskKey, Task> = hashMapOf(
    TaskKey(51L, TaskType.WATER) to Task(TaskKey(51L, TaskType.WATER)),
    TaskKey(51L, TaskType.PRUNE) to Task(TaskKey(51L, TaskType.PRUNE))
)

val tasksWithPlant:HashMap<TaskKey, TaskWithPlant> = tasks.map {
    it.key to TaskWithPlant(it.value, plants[it.key.plantId]!!.commonName)
}.toMap(HashMap())

val plantOneWateringTaskDefaultSchedule = Task(TaskKey(1, TaskType.WATER))
val plantOnePruningTaskDefaultSchedule = Task(TaskKey(1, TaskType.PRUNE))
//val plantOneWateringTaskExpectedSchedule = plantOneWateringTaskDefaultSchedule.apply {  schedule = wateringOneExpectedSchedule }


val wateringOneExpectedSchedule = Schedule(listOf(Day.MONDAY, Day.TUESDAY), null, 12, 0, TaskOccurrence.ONCE)