package com.nkonda.greenthumb.data.source.remote

import com.nkonda.greenthumb.data.Result

interface IRemoteDataSource {
    suspend fun searchPlantByName(name: String): Result<List<PlantSummary>>

    suspend fun getPlantById(plantId: Long): Result<PlantDetails>
}