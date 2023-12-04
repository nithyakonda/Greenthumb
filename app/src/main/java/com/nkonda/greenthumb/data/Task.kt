package com.nkonda.greenthumb.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity( tableName = "tasks" )
data class Task constructor(
    @ColumnInfo (name = "plant_id") val plantId: String,
    @ColumnInfo (name = "completed") val isCompleted: Boolean,
    val type: String, // todo change to enum of water/prune
    val day: String,
    val time: String,
    val repeats: String,
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo (name = "custom_type") val customType: String = ""
) {
}