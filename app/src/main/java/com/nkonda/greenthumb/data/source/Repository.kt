package com.nkonda.greenthumb.data.source

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.data.source.local.ILocalDataSource
import com.nkonda.greenthumb.data.source.remote.IRemoteDataSource
import com.nkonda.greenthumb.data.source.remote.asDomainModel
import com.nkonda.greenthumb.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Repository constructor(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): IRepository {

    override fun observePlants(): LiveData<Result<List<Plant>>> {
        wrapEspressoIdlingResource {
            return localDataSource.observePlants()
        }
    }

    override suspend fun getPlants(): Result<List<Plant>> {
        wrapEspressoIdlingResource {
            return localDataSource.getPlants()
        }
    }

    override suspend fun getPlantById(plantId: Long): Pair<Result<Plant?>, Boolean> {
        wrapEspressoIdlingResource {
            // check in db first before fetching from network
            return if (localDataSource.hasPlant(plantId)) {
                Pair(localDataSource.getPlantById(plantId), true)
            } else {
                when (val remoteResult = remoteDataSource.getPlantById(plantId)) {
                    is Result.Loading -> Pair(Result.Loading, false)
                    is Result.Success -> Pair(Result.Success(remoteResult.data.asDomainModel()), false)
                    is Result.Error -> Pair(remoteResult, false)
                }
            }
        }
    }


    override suspend fun savePlant(plant: Plant): Result<Unit> {
        wrapEspressoIdlingResource {
            return localDataSource.savePlant(plant)
        }
    }

    override suspend fun deletePlant(plantId: Long): Result<Unit> {
        wrapEspressoIdlingResource {
            return localDataSource.deletePlant(plantId)
        }
    }

    override fun searchPlantByImage(image: Bitmap): Result<List<Plant>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchPlantByName(name: String): Result<List<PlantSummary>> {
        wrapEspressoIdlingResource {
            return remoteDataSource.searchPlantByName(name)
        }
    }

    /*----------------------------------------------------------------------------------------*/

    override suspend fun saveTask(task: Task): Result<Unit> {
        wrapEspressoIdlingResource {
            return localDataSource.saveTask(task)
        }
    }

    override fun getTasks(): LiveData<Result<List<Task>>> {
        TODO("Get tasks from Local")
    }



    override fun updateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override fun completeTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskKey: TaskKey): Result<Unit> {
        wrapEspressoIdlingResource {
            return localDataSource.deleteTask(taskKey)
        }
    }

    override suspend fun getUniqueTasks(plantId: Long): Map<TaskType, Task> {
        wrapEspressoIdlingResource {
            return localDataSource.getUniqueTasks(plantId)
        }
    }
}