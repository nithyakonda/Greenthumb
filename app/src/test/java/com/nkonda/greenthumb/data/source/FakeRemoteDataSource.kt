package com.nkonda.greenthumb.data.source

import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.remote.IRemoteDataSource
import com.nkonda.greenthumb.data.source.remote.PlantDetails
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import java.lang.Exception

class FakeRemoteDataSource: IRemoteDataSource {
    private var shouldReturnError = false

    fun setReturnError(value : Boolean) {
        shouldReturnError = value
    }
    override suspend fun searchPlantByName(name: String): Result<List<PlantSummary>> {
        return if (!shouldReturnError) {
            val result = plantSummaries.filter { it.commonName == name }
            Result.Success(result)
        } else {
            Result.Error(Exception("Network error"))
        }
    }

    override suspend fun getPlantById(plantId: Long): Result<PlantDetails> {
        if (!shouldReturnError) {
            return Result.Success(
                PlantDetails(plantId,
                "findOne",
                "sName1",
                "annual",
                1,
                listOf(1,2),
                2,
                listOf("April", "May"),
                "yearly",
                "url1",
                "url1",
                "description1"
            ))
        } else {
            return Result.Error(Exception("Not found"))
        }
    }
}