package com.nkonda.greenthumb.data.source.remote

import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.Result.Error
import com.nkonda.greenthumb.data.Result.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class RemoteDataSource constructor(val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) :
    IRemoteDataSource {
    override suspend fun searchPlantByName(name: String): Result<List<PlantSummary>> = withContext(ioDispatcher) {
            return@withContext try {
                val response = PlantInfoApi.retrofitService.searchPlantByName(name)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Timber.i("Found ${responseBody.data.size} results")
                        Success(responseBody.data)
                    } else {
                        Timber.e("Null response body")
                        Error(Exception("Null response body"))
                    }
                } else {
                    Timber.i("Network request failed")
                    Error(Exception("Network request failed"))
                }
            } catch (e: Exception) {
                Timber.e(e.stackTraceToString())
                Error(e)
            }
        }

    override suspend fun getPlantById(plantId: Long): Result<PlantDetails> = withContext(ioDispatcher) {
        return@withContext try {
            val response = PlantInfoApi.retrofitService.getPlantById(plantId)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Timber.i("Received response \n $responseBody")
                    Success(responseBody)
                } else {
                    Timber.e("Null response body")
                    Error(Exception("Null response body"))
                }
            } else {
                Timber.i("Network request failed")
                Error(Exception("Network request failed"))
            }
        } catch (e: Exception) {
            Error(e)
        }
    }
}