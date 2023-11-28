package com.nkonda.greenthumb.data.source

import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.remote.IRemoteDataSource
import com.nkonda.greenthumb.data.source.remote.PlantDetails
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import java.lang.Exception

class FakeRemoteDataSource: IRemoteDataSource {
    var plantSummaries: List<PlantSummary> = listOf(
        PlantSummary(1, "cName1", "sName1", "annual", "url1"),
        PlantSummary(2, "cName2", "sName2", "perennial", "url2"),
        PlantSummary(3, "cName3", "sName3", "annual", "url3"),
        PlantSummary(4, "cName3", "sName4", "annual", "url4")
    )
    private var shouldReturnError = false

    fun setReturnError(value : Boolean) {
        shouldReturnError = value
    }
    override suspend fun searchPlantByName(name: String): Result<List<PlantSummary>> {
        if (!shouldReturnError) {
            val result = plantSummaries.filter { it.commonName.contains(name) }
            if (result.isNotEmpty()) {
                return Result.Success(result)
            }
            return Result.Error(Exception("Not found"))
        } else {
            return Result.Error(Exception("Network error"))
        }
    }

    override suspend fun getPlantById(plantId: Long): Result<PlantDetails> {
        if (!shouldReturnError) {
            return Result.Success(
                PlantDetails(plantId,
                "cName1",
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