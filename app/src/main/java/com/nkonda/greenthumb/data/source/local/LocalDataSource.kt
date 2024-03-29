package com.nkonda.greenthumb.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.Result.Success
import com.nkonda.greenthumb.data.Result.Error
import com.nkonda.greenthumb.util.getToday
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LocalDataSource constructor(
    private val plantsDao: PlantsDao,
    private val tasksDao: TasksDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ILocalDataSource{
    override suspend fun savePlant(plant: Plant): Result<Unit> = withContext(ioDispatcher) {
        try {
            plantsDao.insertPlant(plant)
            Success(Unit)
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Error(Exception(e.message))
        }
    }

    override suspend fun deletePlant(plantId: Long): Result<Int> = withContext(ioDispatcher) {
         try {
            Success(plantsDao.deletePlantById(plantId))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Error(Exception(e.message))
        }
    }

    override fun observePlants(): LiveData<Result<List<Plant>>> {
        return try {
            plantsDao.observePlants().map {
                Success(it)
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            MutableLiveData<Result<List<Plant>>>().apply {
                value = Error(Exception(e.message))
            }
        }
    }

    override suspend fun getPlants(): Result<List<Plant>> {
        return try {
            Success(plantsDao.getPlants())
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Error(e)
        }
    }

    override suspend fun getPlantById(plantId: Long): Result<Plant> {
        return try {
            plantsDao.getPlantsById(plantId)?.let { Success(it) } ?: Error(Exception("Not found"))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Error(e)
        }
    }

    override suspend fun hasPlant(plantId: Long): Boolean = withContext(ioDispatcher){
        return@withContext try {
            plantsDao.hasPlant(plantId)
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            false
        }
    }

    /*----------------------------------------------------------------------------------------*/

    override suspend fun saveTask(task: Task): Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            Success(tasksDao.insertTask(task))
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Error(e)
        }
    }

    override suspend fun deleteTask(taskKey: TaskKey): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (tasksDao.deleteTask(taskKey.plantId, taskKey.taskType) == 1) {
                Success(Unit)
            } else {
                Error(Exception("Nothing to delete"))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Error(Exception(e.message))
        }
    }

    override suspend fun updateSchedule(taskKey: TaskKey, schedule: Schedule): Result<Unit> = withContext(ioDispatcher){
        try {
            if(tasksDao.updateSchedule(taskKey.plantId, taskKey.taskType, schedule) == 1) {
                Success(Unit)
            } else {
                Error(java.lang.Exception("Nothing to update"))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Error(Exception(e.message))
        }
    }

    override suspend fun updateCompleted(taskKey: TaskKey, isCompleted: Boolean): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (tasksDao.updateCompleted(taskKey.plantId, taskKey.taskType, isCompleted) == 1) {
                Success(Unit)
            } else {
                Error(java.lang.Exception("Nothing to update"))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Error(Exception(e.message))
        }
    }

    override fun observeTask(taskKey: TaskKey): LiveData<Result<Task?>> {
        return try {
            tasksDao.observeTask(taskKey.plantId, taskKey.taskType).map {
                it?.let { Success(it) } ?: Error(Exception("Not found"))
            }
        } catch (e: java.lang.Exception) {
            Timber.e(e.stackTraceToString())
            MutableLiveData<Result<Task?>>().apply {
                value = Error(Exception(e.message))
            }
        }
    }

    override fun observeActiveTasks(): LiveData<Result<List<TaskWithPlant>>> {
        return try {
            tasksDao.observeTasks().map { allTasks ->
                val currTasks = allTasks.filter {
                    val schedule = it.task.schedule
                    val (day, month) = getToday()
                    schedule.days.contains(day) ||
                            schedule.months.contains(month)
                }
                Success(currTasks)
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            MutableLiveData<Result<List<TaskWithPlant>>>().apply {
                value = Error(Exception(e.message))
            }
        }
    }

    override suspend fun getTasks(): Result<List<TaskWithPlant>> {
        return try {
            Success(tasksDao.getTasks())
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Error(e)
        }
    }

    override suspend fun hasTask(taskKey: TaskKey): Boolean {
        return try {
            tasksDao.hasTask(taskKey.plantId, taskKey.taskType) == 1
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            false
        }
    }
}