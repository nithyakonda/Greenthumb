package com.nkonda.greenthumb.data.source

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.Task
import com.nkonda.greenthumb.data.source.remote.DefaultImage
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import java.lang.Exception

class FakeRepository: IRepository {
    private var shouldReturnError = false

    fun setReturnError(value : Boolean) {
        shouldReturnError = value
    }
    
    override fun getTasks(): LiveData<Result<List<Task>>> {
        TODO("Not yet implemented")
    }

    override fun saveTask(task: Task) {
        TODO("Not yet implemented")
    }

    override fun updateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override fun completeTask(task: Task) {
        TODO("Not yet implemented")
    }

    override fun deleteTask(task: Task) {
        TODO("Not yet implemented")
    }

    override fun getMyPlants(): LiveData<Result<List<Plant>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlantById(plantId: Long): LiveData<Result<Plant>> {
        TODO("Not yet implemented")
    }

    override fun savePlant(plant: Plant) {
        TODO("Not yet implemented")
    }

    override fun deletePlant(plantId: String) {
        TODO("Not yet implemented")
    }

    override fun searchPlantByImage(image: Bitmap): Result<List<Plant>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchPlantByName(name: String): Result<List<PlantSummary>> {
        return if (!shouldReturnError) {
            val result = plantSummaries.filter { it.commonName == name }
            Result.Success(result)
        } else {
            Result.Error(Exception("Network error"))
        }
    }
}