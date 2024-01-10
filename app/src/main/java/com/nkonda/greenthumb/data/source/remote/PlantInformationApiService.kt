package com.nkonda.greenthumb.data.source.remote

import com.nkonda.greenthumb.BuildConfig
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://perenual.com/api/"
private const val TIMEOUT_SECONDS = 30L

val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
    .addInterceptor(loggingInterceptor)
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(PruningCountAdapter())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(client)
    .baseUrl(BASE_URL)
    .build()

interface PlantInformationApiService {
    companion object {
        const val PLANT_INFO_API_KEY =  BuildConfig.PLANT_INFO_API_KEY
    }
    @GET("species-list")
    suspend fun searchPlantByName(
        @Query("q") name: String,
        @Query("key") apiKey: String = PLANT_INFO_API_KEY
    ): Response<SearchResult>

    @GET("species/details/{plantId}")
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

class PruningCountAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): List<PruningCount>? {
        val moshiForPruningCount = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return when (reader.peek()) {
            JsonReader.Token.BEGIN_OBJECT -> {
                val jsonAdapter = moshiForPruningCount.adapter(PruningCount::class.java)
                val singleObject = jsonAdapter.fromJson(reader)
                listOfNotNull(singleObject)
            }
            JsonReader.Token.BEGIN_ARRAY -> {
                val listType = Types.newParameterizedType(List::class.java, PruningCount::class.java)
                val adapter: JsonAdapter<List<PruningCount>> = moshiForPruningCount.adapter(listType)
                val list = adapter.fromJson(reader)
                // Check if the list is not empty and return the first item; otherwise, return null
                emptyList()
            }
            else -> {
                null
            }
        }
    }
}
