package com.nkonda.greenthumb.data.source

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.source.remote.PlantSummary

interface IRepository {

    /**
     * Plants
     */
    fun observePlants(): LiveData<Result<List<Plant>>>

    suspend fun getPlants(): Result<List<Plant>>

    suspend fun getPlantById(plantId: Long): Pair<Result<Plant?>, Boolean>

    suspend fun savePlant(plant: Plant): Result<Unit>

    suspend fun deletePlant(plantId: Long): Result<Unit>

    fun searchPlantByImage(image: Bitmap): Result<List<Plant>>

    suspend fun searchPlantByName(name: String): Result<List<PlantSummary>>

    /**
     * Tasks
     */
    fun getTasks(): LiveData<Result<List<Task>>>

    suspend fun saveTask(task: Task): Result<Unit>

    suspend fun updateSchedule(taskKey: TaskKey, schedule: Schedule): Result<Unit>

    suspend fun completeTask(taskKey: TaskKey, isCompleted: Boolean): Result<Unit>

    suspend fun deleteTask(taskKey: TaskKey): Result<Unit>

    fun observeTask(taskKey: TaskKey): LiveData<Result<Task?>>

    fun observeTasks(): LiveData<Result<List<TaskWithPlant>>>
}