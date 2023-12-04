package com.nkonda.greenthumb.data.source.local

import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result

interface ILocalDataSource {
    suspend fun savePlant(plant: Plant): Result<Unit>

    fun deletePlant(plantId: String)

    fun getMyPlants(): LiveData<Result<List<Plant>>>
}