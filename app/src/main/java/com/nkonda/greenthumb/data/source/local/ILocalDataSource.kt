package com.nkonda.greenthumb.data.source.local

import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.Task

interface ILocalDataSource {
    suspend fun savePlant(plant: Plant): Result<Unit>

    suspend fun deletePlant(plantId: Long): Result<Unit>

    fun observePlants(): LiveData<Result<List<Plant>>>

    suspend fun getPlants(): Result<List<Plant>>

    suspend fun getPlantById(plantId: Long): Result<Plant?>

    suspend fun hasPlant(plantId: Long): Boolean

    /*----------------------------------------------------------------------------------------*/

    suspend fun saveTask(task: Task): Result<Unit>
}