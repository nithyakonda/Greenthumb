package com.nkonda.greenthumb.data.source

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Task
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.remote.IRemoteDataSource
import com.nkonda.greenthumb.data.source.remote.asDomainModel
import com.nkonda.greenthumb.util.wrapEspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Repository constructor(
    private val remoteDataSource: IRemoteDataSource,
//    private val localDataSource: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO): IRepository {
    override fun getTasks(): LiveData<Result<List<Task>>> {
        TODO("Get tasks from Local")
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
        TODO("Get plants from local")
    }

    override suspend fun getPlantById(plantId: Long): LiveData<Result<Plant>> = liveData {
        wrapEspressoIdlingResource {
            val existsInLocal = false // TODO - add implementation
            if (!existsInLocal) {
                when (val result = remoteDataSource.getPlantById(plantId)) {
                    is Result.Loading -> emit(Result.Loading)
                    is Result.Success -> {
                        val plant = result.data.asDomainModel()
                        emit(Result.Success(plant))
                    }
                    is Result.Error -> emit(result)
                }
            }
        }
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
        wrapEspressoIdlingResource {
            return remoteDataSource.searchPlantByName(name)
        }
    }
}