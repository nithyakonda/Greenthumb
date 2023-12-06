package com.nkonda.greenthumb.data.source.testdoubles

import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.remote.IRemoteDataSource
import com.nkonda.greenthumb.data.source.remote.Images
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
                listOf("sName1"),
                "annual",
                "Easy",
                listOf("part-shade", "full-sun"),
                "High",
                listOf("April", "May"),
                Images("thumbnail", "originalUrl"),
                "description1",
            ))
        } else {
            return Result.Error(Exception("Not found"))
        }
    }
}