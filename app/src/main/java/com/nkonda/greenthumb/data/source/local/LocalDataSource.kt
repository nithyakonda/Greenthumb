package com.nkonda.greenthumb.data.source.local

import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class LocalDataSource constructor(
    private val plantsDao: PlantsDao,
    private val tasksDao: TasksDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ILocalDataSource {
    override suspend fun savePlant(plant: Plant): Result<Unit> = withContext(ioDispatcher) {
        return@withContext  try {
            plantsDao.insertPlant(plant)
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Result.Error(Exception(e.message))
        }
    }

    override fun deletePlant(plantId: String) {
        TODO("Not yet implemented")
    }

    override fun getMyPlants(): LiveData<Result<List<Plant>>> {
        TODO("Not yet implemented")
    }
}