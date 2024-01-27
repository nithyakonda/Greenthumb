package com.nkonda.greenthumb.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.nkonda.greenthumb.data.*

@Dao
interface TasksDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertTask(task: Task)

    @Query("UPDATE tasks SET completed = :completed WHERE plant_id = :plantId AND task_type = :taskType")
    suspend fun updateCompleted(plantId: Long, taskType: TaskType, completed: Boolean): Int

    @Query("UPDATE tasks SET schedule = :schedule WHERE plant_id = :plantId AND task_type = :taskType")
    suspend fun updateSchedule(plantId: Long, taskType: TaskType, schedule: Schedule): Int

    @Query("DELETE FROM tasks WHERE plant_id = :plantId")
    suspend fun deleteTasksByPlantId(plantId: Long): Int

    @Query("DELETE FROM tasks WHERE plant_id = :plantId AND task_type = :taskType")
    suspend fun deleteTask(plantId: Long, taskType: TaskType): Int

    @Query("SELECT tasks.*, plants.common_name, plants.image FROM tasks INNER JOIN plants ON tasks.plant_id = plants.id")
    fun observeTasks(): LiveData<List<TaskWithPlant>>

    @Query("SELECT * FROM tasks")
    suspend fun getTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE plant_id = :plantId AND task_type = :taskType")
    suspend fun getTask(plantId: Long, taskType: TaskType): Task?

    @Query("SELECT * FROM tasks WHERE plant_id = :plantId AND task_type = :taskType")
    fun observeTask(plantId: Long, taskType: TaskType): LiveData<Task?>
}