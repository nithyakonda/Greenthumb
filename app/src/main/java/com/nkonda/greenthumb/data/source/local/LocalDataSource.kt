package com.nkonda.greenthumb.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.Result.Success
import com.nkonda.greenthumb.data.Result.Error
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
            Error(e)
        }
    }
}