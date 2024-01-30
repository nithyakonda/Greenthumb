package com.nkonda.greenthumb.data.source.testdoubles

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.source.remote.PlantSummary
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

    override fun observePlants(): LiveData<Result<List<Plant>>> {
        val result = MutableLiveData<Result<List<Plant>>>()
        if (!shouldReturnError) {
            result.value = Result.Success(plants.values as List<Plant>)
        } else {
            result.value = Result.Error(Exception("DB Error"))
        }
        return result
    }

    override suspend fun getPlants(): Result<List<Plant>> {
        return if(!shouldReturnError) {
            Result.Success(plants.values as List<Plant>)
        } else {
            Result.Error(Exception("DB Error"))
        }
    }

    override suspend fun getPlantById(plantId: Long): Pair<Result<Plant?>, Boolean> {
        return if(!shouldReturnError) {
            val result = plants.getOrDefault(plantId, null)
            Pair(Result.Success(result), getFromDb)
        } else {
            Pair(Result.Error(Exception("Network error")), getFromDb)
        }
    }

    override suspend fun savePlant(plant: Plant): Result<Unit> {
        return if(!shouldReturnError) {
            plants[plant.id] = plant
            Result.Success(Unit)
        } else {
            Result.Error(Exception("DB Error"))
        }
    }

    override suspend fun deletePlant(plantId: Long): Result<Int> {
        return if(!shouldReturnError) {
            plants.getOrDefault(plantId, null)?.let {
                plants.remove(plantId)
                Result.Success(1)
            } ?: Result.Success(0)
        } else {
            Result.Error(Exception("DB Error"))
        }
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

    override suspend fun saveTask(task: Task): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSchedule(taskKey: TaskKey, schedule: Schedule): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(taskKey: TaskKey, isCompleted: Boolean): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskKey: TaskKey): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskKey: TaskKey): LiveData<Result<Task?>> {
        TODO("Not yet implemented")
    }

    override fun observeActiveTasks(): LiveData<Result<List<TaskWithPlant>>> {
        TODO("Not yet implemented")
    }

}