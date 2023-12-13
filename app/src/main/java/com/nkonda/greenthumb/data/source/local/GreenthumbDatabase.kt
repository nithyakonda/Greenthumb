package com.nkonda.greenthumb.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Task
import com.nkonda.greenthumb.util.ScheduleConverter
import com.nkonda.greenthumb.util.StringListConverter

const val DB_NAME = "Greenthumb.db"
@Database(entities = [Plant::class, Task::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class, ScheduleConverter::class)
abstract class GreenthumbDatabase: RoomDatabase() {
    companion object {
        fun createPlantsDao(context: Context): PlantsDao {
            return Room.databaseBuilder(
                context.applicationContext,
                GreenthumbDatabase::class.java,
                DB_NAME
            ).build().plantsDao()
        }

        fun createTasksDao(context: Context): TasksDao {
            return Room.databaseBuilder(
                context.applicationContext,
                GreenthumbDatabase::class.java,
                DB_NAME
            ).build().tasksDao()
        }
    }
    abstract fun plantsDao(): PlantsDao

    abstract fun tasksDao(): TasksDao
}