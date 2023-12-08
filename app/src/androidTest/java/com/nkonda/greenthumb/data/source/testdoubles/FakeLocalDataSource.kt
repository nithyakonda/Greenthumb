package com.nkonda.greenthumb.data.source.testdoubles

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result
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

    override suspend fun getPlantById(plantId: Long): Result<Plant?> {
        if (shouldReturnError) {
            return Result.Error(Exception(context.getString(R.string.test_error_db_error)))
        }
        return Result.Success(database.plantsDao().getPlantsById(plantId))
    }

    override suspend fun hasPlant(plantId: Long): Boolean {
        return if (shouldReturnError) false
        else {
            database.plantsDao().hasPlant(plantId)
        }
    }
}