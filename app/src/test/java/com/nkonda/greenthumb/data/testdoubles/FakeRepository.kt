package com.nkonda.greenthumb.data.source.testdoubles

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.Task
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.data.testdoubles.plants
import com.nkonda.greenthumb.data.testdoubles.plantSummaries
import java.lang.Exception

class FakeRepository: IRepository {
    private var shouldReturnError = false
    private var getFromDb = false

    fun setReturnError(value : Boolean) {
        shouldReturnError = value
    }


    fun setGetFromDb(value: Boolean) {
        getFromDb = value
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

    override fun observePlants(): LiveData<Result<List<Plant>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlants(): Result<List<Plant>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlantById(plantId: Long): Pair<Result<Plant?>, Boolean> {
        return if(!shouldReturnError) {
            val result = plants.find { it.id == plantId }
            Pair(Result.Success(result), getFromDb)
        } else {
            Pair(Result.Error(Exception("Network error")), getFromDb)
        }
    }

    override suspend fun savePlant(plant: Plant): Result<Unit> {
        return if (shouldReturnError) {
            Result.Error(Exception("DB Error"))
        } else {
            plants.add(plant)
            Result.Success(Unit)
        }
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