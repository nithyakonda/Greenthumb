package com.nkonda.greenthumb.data.testdoubles

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
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

    override fun observePlants(): LiveData<Result<List<Plant>>> = liveData {
        emit(
            if (!shouldReturnError) {
                Result.Success(plants.values.toList())
            } else {
                Result.Error(Exception("DB error"))
            }
        )
    }

    override suspend fun getPlants(): Result<List<Plant>> {
        return if (!shouldReturnError) {
            Result.Success(plants.values.toList())
        } else {
            Result.Error(Exception("DB error"))
        }
    }

    override suspend fun getPlantById(plantId: Long): Pair<Result<Plant?>, Boolean> {
        val result = if(!shouldReturnError) {
            if (getFromDb) {
                Result.Success(localPlants[plantId])
            } else {
                Result.Success(remotePlants[plantId])
            }
        } else {
            Result.Error(Exception("Network error"))
        }
        return Pair(result, getFromDb)
    }

    override suspend fun savePlant(plant: Plant): Result<Unit> {
        return if (shouldReturnError) {
            Result.Error(Exception("DB Error"))
        } else {
            localPlants[plant.id] = plant
            Result.Success(Unit)
        }
    }

    override suspend fun deletePlant(plantId: Long): Result<Int> {
        return if (shouldReturnError) {
            Result.Error(Exception("DB Error"))
        } else {
            if (localPlants.containsKey(plantId)) {
                localPlants.remove(plantId)
                Result.Success(1)
            } else {
                Result.Success(0)
            }
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
        return if (!shouldReturnError) {
            tasks[task.key] = task
            Result.Success(Unit)
        } else {
            Result.Error(Exception("DB error"))
        }
    }

    override suspend fun updateSchedule(taskKey: TaskKey, newSchedule: Schedule): Result<Unit> {
        return if (!shouldReturnError) {
            if (tasks.containsKey(taskKey)) {
                tasks[taskKey]?.schedule = newSchedule
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Nothing to update"))
            }

        } else {
            Result.Error(Exception("DB error"))
        }
    }

    override suspend fun completeTask(taskKey: TaskKey, isCompleted: Boolean): Result<Unit> {
        return if (!shouldReturnError) {
            if (tasks.containsKey(taskKey)) {
                tasks[taskKey]?.completed = isCompleted
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Nothing to update"))
            }

        } else {
            Result.Error(Exception("DB error"))
        }
    }

    override suspend fun deleteTask(taskKey: TaskKey): Result<Unit> {
        return if (!shouldReturnError) {
            if (tasks.containsKey(taskKey)) {
                tasks.remove(taskKey)
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Nothing to delete"))
            }

        } else {
            Result.Error(Exception("DB error"))
        }
    }

    override fun observeTask(taskKey: TaskKey): LiveData<Result<Task?>> = liveData {
        emit(
            if (!shouldReturnError) {
                Result.Success(tasks.getOrDefault(taskKey, null))
            } else {
                Result.Error(Exception("DB error"))
            }
        )
    }


    override fun observeTasks(): LiveData<Result<List<TaskWithPlant>>> = liveData {
        emit(
            if (!shouldReturnError) {
                Result.Success(tasksWithPlant.values.toList())
            } else {
                Result.Error(Exception("DB error"))
            }
        )
    }

}