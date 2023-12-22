package com.nkonda.greenthumb.data.source.local

import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.*

interface ILocalDataSource {
    suspend fun savePlant(plant: Plant): Result<Unit>

    suspend fun deletePlant(plantId: Long): Result<Unit>

    fun observePlants(): LiveData<Result<List<Plant>>>

    suspend fun getPlants(): Result<List<Plant>>

    suspend fun getPlantById(plantId: Long): Result<Plant?>

    suspend fun hasPlant(plantId: Long): Boolean

    /*----------------------------------------------------------------------------------------*/

    suspend fun saveTask(task: Task): Result<Unit>

    suspend fun updateSchedule(taskKey: TaskKey, schedule: Schedule): Result<Unit>

    suspend fun updateCompleted(taskKey: TaskKey, isCompleted: Boolean): Result<Unit>

    suspend fun deleteTask(taskKey: TaskKey): Result<Unit>

    fun observeTask(taskKey: TaskKey): LiveData<Result<Task?>>

    fun observeTasks(): LiveData<Result<List<TaskWithPlant>>>
}