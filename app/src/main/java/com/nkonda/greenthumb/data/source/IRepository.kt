package com.nkonda.greenthumb.data.source

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Task
import com.nkonda.greenthumb.data.source.remote.PlantSummary

interface IRepository {
    /**
     * Tasks
     */
    fun getTasks(): LiveData<Result<List<Task>>>

    fun saveTask(task: Task)

    fun updateTask(task: Task)

    fun completeTask(task: Task)

    fun deleteTask(task: Task)

    /**
     * Plants
     */

    fun getMyPlants(): LiveData<Result<List<Plant>>>

    suspend fun getPlantById(plantId: Long): LiveData<Result<Plant>>

    fun savePlant(plant: Plant)

    fun deletePlant(plantId: String)

    fun searchPlantByImage(image: Bitmap): Result<List<Plant>>

    suspend fun searchPlantByName(name: String): Result<List<PlantSummary>>
}