package com.nkonda.greenthumb.data.source

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Task
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

    fun updateTask(task: Task)

    fun completeTask(task: Task)

    fun deleteTask(task: Task)
}