package com.nkonda.greenthumb.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.nkonda.greenthumb.data.Plant

@Dao
interface PlantsDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertPlant(plant: Plant)

    @Query("SELECT COUNT(*) FROM plants")
    fun getPlantsCount(): Int

    @Query("SELECT * FROM plants")
    suspend fun getPlants(): List<Plant>

    @Query("SELECT * FROM Plants")
    fun observePlants(): LiveData<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantsById(plantId: Long): Plant?


    @Query("SELECT COUNT(*) FROM plants WHERE id = :plantId")
    suspend fun hasPlant(plantId: Long): Boolean
}