package com.nkonda.greenthumb.data.source.remote

import androidx.viewbinding.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://perenual.com/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface PlantInformationApiService {
    companion object {
        const val PLANT_INFO_API_KEY = "BuildConfig.PLANT_INFO_API_KEY"
    }
    @GET("species-list")
    suspend fun searchPlantByName(
        @Query("q") name: String,
        @Query("key") apiKey: String = PLANT_INFO_API_KEY
    ): Response<List<PlantSummary>>

    @GET("species/details/{plantID}")
    suspend fun getPlantById(
        @Path("plantId") plantId: Long,
        @Query("key") apiKey: String = PLANT_INFO_API_KEY
    ) : Response<PlantDetails>
}

object PlantInfoApi {
    val retrofitService : PlantInformationApiService by lazy {
        retrofit.create(PlantInformationApiService::class.java)
    }
}
