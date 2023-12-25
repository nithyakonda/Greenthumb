package com.nkonda.greenthumb.data.source.testdoubles

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.source.local.GreenthumbDatabase
import com.nkonda.greenthumb.data.source.local.ILocalDataSource
import java.lang.Exception

class FakeLocalDataSource : ILocalDataSource{
    private val context: Context = ApplicationProvider.getApplicationContext()
    private var database: GreenthumbDatabase = Room.inMemoryDatabaseBuilder(
        context ,
        GreenthumbDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    private var shouldReturnError = false
    fun setReturnError(value : Boolean) {
        shouldReturnError = value
    }
    override suspend fun savePlant(plant: Plant): Result<Unit> {
        if (shouldReturnError) {
            return Result.Error(Exception(context.getString(R.string.test_error_db_error)))
        }
        return Result.Success(database.plantsDao().insertPlant(plant))
    }

    override suspend fun deletePlant(plantId: Long): Result<Unit> {
        if (shouldReturnError) {
            return Result.Error(Exception(context.getString(R.string.test_error_db_error)))
        }
        return if (database.plantsDao().deletePlantById(plantId) == 1) {
            Result.Success(Unit)
        } else {
            Result.Error(Exception(context.getString(R.string.test_error_nothing_to_delete)))
        }
    }

    override fun observePlants(): LiveData<Result<List<Plant>>> {
        if (shouldReturnError) {
            return MutableLiveData<Result<List<Plant>>>().apply {
                value = Result.Error(Exception(context.getString(R.string.test_error_db_error)))
            }
        }
        return database.plantsDao().observePlants().map { Result.Success(it) }
    }

    override suspend fun getPlants(): Result<List<Plant>> {
        if (shouldReturnError) {
            return Result.Error(Exception(context.getString(R.string.test_error_db_error)))
        }
        return Result.Success(database.plantsDao().getPlants())
    }

    override suspend fun getPlantById(plantId: Long): Result<Plant> {
        if (shouldReturnError) {
            return Result.Error(Exception(context.getString(R.string.test_error_db_error)))
        }
        return database.plantsDao().getPlantsById(plantId)?. let { Result.Success(it)} ?: Result.Error(Exception("Not found"))
    }

    override suspend fun hasPlant(plantId: Long): Boolean {
        return if (shouldReturnError) false
        else {
            database.plantsDao().hasPlant(plantId)
        }
    }

    override suspend fun saveTask(task: Task): Result<Unit> {
        if (shouldReturnError) {
            return Result.Error(Exception(context.getString(R.string.test_error_db_error)))
        }
        return Result.Success(database.tasksDao().insertTask(task))
    }

    override suspend fun updateSchedule(taskKey: TaskKey, schedule: Schedule): Result<Unit> {
        if (shouldReturnError) {
            return Result.Error(Exception(context.getString(R.string.test_error_db_error)))
        }
        return if (database.tasksDao().updateSchedule(taskKey.plantId, taskKey.taskType, schedule) == 1) {
            Result.Success(Unit)
        } else {
            Result.Error(Exception("Nothing to update"))
        }
    }

    override suspend fun updateCompleted(taskKey: TaskKey, isCompleted: Boolean): Result<Unit> {
        if (shouldReturnError) {
            return Result.Error(Exception(context.getString(R.string.test_error_db_error)))
        }
        return if (database.tasksDao().updateCompleted(taskKey.plantId, taskKey.taskType, isCompleted) == 1) {
            Result.Success(Unit)
        } else {
            Result.Error(Exception("Nothing to update"))
        }
    }

    override suspend fun deleteTask(taskKey: TaskKey): Result<Unit> {
        if (shouldReturnError) {
            return Result.Error(Exception(context.getString(R.string.test_error_db_error)))
        }
        return if (database.tasksDao().deleteTask(taskKey.plantId, taskKey.taskType) == 1) {
            Result.Success(Unit)
        } else {
            Result.Error(Exception(context.getString(R.string.test_error_nothing_to_delete)))
        }
    }

    override fun observeTask(taskKey: TaskKey): LiveData<Result<Task?>> {
        if (shouldReturnError) {
            return MutableLiveData<Result<Task?>>().apply {
                value = Result.Error(Exception(context.getString(R.string.test_error_db_error)))
            }
        }
        return database.tasksDao().observeTask(taskKey.plantId, taskKey.taskType).map { Result.Success(it) }
    }

    override fun observeTasks(): LiveData<Result<List<TaskWithPlant>>> {
        if (shouldReturnError) {
            return MutableLiveData<Result<List<TaskWithPlant>>>().apply {
                value = Result.Error(Exception(context.getString(R.string.test_error_db_error)))
            }
        }
        return database.tasksDao().observeTasks().map { Result.Success(it) }
    }

    override suspend fun getTasks(): Result<List<Task>> {
        if (shouldReturnError) {
            return Result.Error(Exception(context.getString(R.string.test_error_db_error)))
        }
        return Result.Success(database.tasksDao().getTasks())
    }
}