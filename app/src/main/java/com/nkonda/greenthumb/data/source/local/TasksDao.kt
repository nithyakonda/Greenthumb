package com.nkonda.greenthumb.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.nkonda.greenthumb.data.Schedule
import com.nkonda.greenthumb.data.Task
import com.nkonda.greenthumb.data.TaskType

@Dao
interface TasksDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertTask(task: Task)

    @Query("UPDATE tasks SET completed = :completed WHERE id = :taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean): Int

    @Query("DELETE FROM tasks WHERE plant_id = :plantId")
    suspend fun deleteTasksByPlantId(plantId: Long): Int

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String): Int

    @Query("SELECT * FROM tasks")
    fun observeTasks(): LiveData<List<Task>?>

    @Query("SELECT * FROM tasks WHERE plant_id = :plantId")
    suspend fun getTasksForPlant(plantId: Long): List<Task>?

    // This is a hack to get a schedule for a plantId/taskType combination which is unique for a schedule
    // i.e. the schedule value is same for all tasks with a given plantId/taskType, so limiting to 1 works
    // Ideally, create a separate table for schedules and have a composite primary key of plantId/taskType
    @Query("SELECT * FROM tasks WHERE plant_id = :plantId AND type = :taskType LIMIT 1")
    suspend fun getTask(plantId: Long, taskType: TaskType): Task?
}