package com.nkonda.greenthumb.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.nkonda.greenthumb.data.Task
import com.nkonda.greenthumb.data.TaskKey
import com.nkonda.greenthumb.data.TaskType

@Dao
interface TasksDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertTask(task: Task)

    @Query("UPDATE tasks SET completed = :completed WHERE plant_id = :plantId AND task_type = :taskType")
    suspend fun updateCompleted(plantId: Long, taskType: TaskType, completed: Boolean): Int

    @Query("DELETE FROM tasks WHERE plant_id = :plantId")
    suspend fun deleteTasksByPlantId(plantId: Long): Int

    @Query("DELETE FROM tasks WHERE plant_id = :plantId AND task_type = :taskType")
    suspend fun deleteTask(plantId: Long, taskType: TaskType): Int

    @Query("SELECT * FROM tasks")
    fun observeTasks(): LiveData<List<Task>?>

    @Query("SELECT * FROM tasks WHERE plant_id = :plantId")
    suspend fun getTasksForPlant(plantId: Long): List<Task>?

    @Query("SELECT * FROM tasks WHERE plant_id = :plantId AND task_type = :taskType")
    suspend fun getTask(plantId: Long, taskType: TaskType): Task?
}