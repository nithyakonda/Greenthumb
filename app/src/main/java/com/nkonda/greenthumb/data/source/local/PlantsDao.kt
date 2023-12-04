package com.nkonda.greenthumb.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import com.nkonda.greenthumb.data.Plant

@Dao
interface PlantsDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertPlant(plant: Plant)
}