package com.nkonda.greenthumb.data.source.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.TaskKey
import com.nkonda.greenthumb.data.TaskType
import com.nkonda.greenthumb.data.source.testdoubles.*
import com.nkonda.greenthumb.data.succeeded
import com.nkonda.greenthumb.util.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: LocalDataSource
    private lateinit var database: GreenthumbDatabase
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            context,
            GreenthumbDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        localDataSource = LocalDataSource(database.plantsDao(),
            database.tasksDao(),
            Dispatchers.Main)
    }

    @After
    fun teardown() {
        database.close()
    }

    /**
     * Test naming convention
     * <method under test>_<given/when>_<then>
     */

    @Test
    fun savePlant_givenNewPlant_isSuccess() = runBlocking {
        localDataSource.savePlant(plantOne)

        val result = localDataSource.getPlants() as Result.Success

        assertThat(result.data[0], `is`(plantOne))
    }

    @Test
    fun savePlant_givenExistingPlant_replacesOldEntry() = runBlocking {
        val plant = plantOne
        localDataSource.savePlant(plant)
        var result = (localDataSource.getPlants() as Result.Success).data[0]
        assertThat(result.commonName, `is`(plantOne.commonName))

        plant.commonName = "newCommonName"
        localDataSource.savePlant(plant)
        result = (localDataSource.getPlants() as Result.Success).data[0]
        assertThat(result.commonName, `is`("newCommonName"))
    }


    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun deletePlantById_givenExistingPlant_returnsSuccess() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.savePlant(plantTwo)
        assertThat((localDataSource.getPlants() as Result.Success).data.size, `is`(2))

        val result = localDataSource.deletePlant(plantOne.id)
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat((localDataSource.getPlants() as Result.Success).data.size, `is`(1))

    }

    @Test
    fun deletePlantById_givenValidPlantWithTasks_deletesTasksAndReturnsSuccess() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        localDataSource.saveTask(plantOnePruningTaskDefaultSchedule)
        assertThat((localDataSource.getPlants() as Result.Success).data.size, `is`(1))
        assertThat((localDataSource.getTasks() as Result.Success).data.size, `is`(2))

        val result = localDataSource.deletePlant(plantOne.id)
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat((localDataSource.getPlants() as Result.Success).data.size, `is`(0))
        assertThat((localDataSource.getTasks() as Result.Success).data.size, `is`(0))
    }

    @Test
    fun deletePlantById_givenNonExistingPlant_returnsError() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.savePlant(plantTwo)
        assertThat((localDataSource.getPlants() as Result.Success).data.size, `is`(2))

        val result = localDataSource.deletePlant(100)
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data, `is`(0))
        assertThat((localDataSource.getPlants() as Result.Success).data.size, `is`(2))
    }

    /*-------------------------------------------------------------------------------------------*/


    @Test
    fun observePlants_givenEmptyTable_returnsEmptyList() {
        val result = localDataSource.observePlants()

        assertThat(result.getOrAwaitValue().succeeded, `is`(true))
        val plants = (result.value as Result.Success).data
        assertThat(plants.isEmpty(), `is`(true))
    }

    @Test
    fun observePlants_givenNonEmptyTable_returnsList() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.savePlant(plantTwo)

        val result = localDataSource.observePlants()

        assertThat(result.getOrAwaitValue().succeeded, `is`(true))
        val plants = (result.value as Result.Success).data

        assertThat(plants.size, `is`(2))
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun getPlants_givenEmptyTable_returnsEmptyList() = runBlocking {
        val result = localDataSource.getPlants()

        assertThat(result.succeeded, `is`(true))
        val plants = (result as Result.Success).data

        assertThat(plants.size, `is`(0))
    }

    @Test
    fun getPlants_givenNonEmptyTable_returnsList() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.savePlant(plantTwo)

        val result = localDataSource.getPlants()
        assertThat(result.succeeded, `is`(true))
        val plants = (result as Result.Success).data

        assertThat(plants.size, `is`(2))
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun getPlantById_givenValidId_returnsSuccess() = runBlocking {
        localDataSource.savePlant(plantOne)

        val result = localDataSource.getPlantById(plantOne.id)
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data!!.id, `is`(plantOne.id))
    }

    @Test
    fun getPlantById_givenInvalidId_returnsNull() = runBlocking {
        localDataSource.savePlant(plantOne)

        val result = localDataSource.getPlantById(plantTwo.id)
        assertThat(result.succeeded, `is`(false))
        result as Result.Error
        assertThat(result.exception.message, `is`(context.getString(R.string.test_error_not_found)))
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun hasPlant_givenValidId_returnsTrue() = runBlocking {
        localDataSource.savePlant(plantOne)

        val result = localDataSource.hasPlant(plantOne.id)
        assertThat(result, `is`(true))
    }

    @Test
    fun hasPlant_givenInvalidId_returnsFalse() = runBlocking {
        localDataSource.savePlant(plantOne)

        val result = localDataSource.hasPlant(plantTwo.id)
        assertThat(result, `is`(false))
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun saveTask_givenNewTask_returnsSuccess() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)

        val result = localDataSource.getTasks()
        assertThat(result.succeeded, `is`(true))
        val tasks = (result as Result.Success).data
        assertThat(tasks.size, `is`(1))
        assertThat(tasks[0].key.plantId, `is`(1))
    }

    @Test
    fun saveTask_givenExistingTask_replacesAndReturnsSuccess() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)

        var tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))
        assertThat(tasks[0].schedule.isSet(), `is`(false))

        val newTask = plantOneWateringTaskDefaultSchedule.copy(schedule = wateringOneExpectedSchedule)
        localDataSource.saveTask(newTask)
        tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))
        assertThat(tasks[0].schedule.isSet(), `is`(true))
    }

    @Test
    fun saveTask_whenPlantNotSaved_returnsError() = runBlocking {
        val result = localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        assertThat(result.succeeded, `is`(false))
    }

    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun updateSchedule_givenValidTaskKey_updatesSchedule() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        var tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))
        assertThat(tasks[0].schedule.isSet(), `is`(false))

        val validTaskKey = TaskKey(plantOne.id, TaskType.WATER)
        val result = localDataSource.updateSchedule(validTaskKey, wateringOneExpectedSchedule)
        assertThat(result.succeeded, `is`(true))
        tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))
        assertThat(tasks[0].schedule.isSet(), `is`(true))
    }

    @Test
    fun updateSchedule_givenInvalidTaskKey_returnsError() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        var tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))
        assertThat(tasks[0].schedule.isSet(), `is`(false))

        val invalidTaskKey = TaskKey(plantOne.id, TaskType.PRUNE)
        val result = localDataSource.updateSchedule(invalidTaskKey, wateringOneExpectedSchedule)
        assertThat(result.succeeded, `is`(false))
        assertThat((result as Result.Error).exception.message, `is`(context.getString(R.string.test_error_nothing_to_update)))
        tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))
        assertThat(tasks[0].schedule.isSet(), `is`(false))
    }

    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun updateCompleted_givenValidTaskKey_returnsSuccess() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        var tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))
        assertThat(tasks[0].completed, `is`(false))

        val validTaskKey = TaskKey(plantOne.id, TaskType.WATER)
        val result = localDataSource.updateCompleted(validTaskKey, true)
        assertThat(result.succeeded, `is`(true))
        tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))
        assertThat(tasks[0].completed, `is`(true))
    }

    @Test
    fun updateCompleted_givenInvalidTaskKey_returnsError() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        var tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))
        assertThat(tasks[0].completed, `is`(false))

        val invalidTaskKey = TaskKey(plantOne.id, TaskType.PRUNE)
        val result = localDataSource.updateCompleted(invalidTaskKey, true)
        assertThat(result.succeeded, `is`(false))
        assertThat((result as Result.Error).exception.message, `is`(context.getString(R.string.test_error_nothing_to_update)))
        tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))
        assertThat(tasks[0].completed, `is`(false))
    }

    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun deleteTask_givenExistingTask_returnsSuccess() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        var tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))

        val validTaskKey = TaskKey(plantOne.id, TaskType.WATER)
        val result = localDataSource.deleteTask(validTaskKey)
        assertThat(result.succeeded, `is`(true))
        tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks.isEmpty(), `is`(true))
    }

    @Test
    fun deleteTask_givenInvalidTaskKey_returnsError() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        var tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))

        val invalidTaskKey = TaskKey(plantOne.id, TaskType.PRUNE)
        val result = localDataSource.deleteTask(invalidTaskKey)
        assertThat(result.succeeded, `is`(false))
        assertThat((result as Result.Error).exception.message, `is`(context.getString(R.string.test_error_nothing_to_delete)))
        tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks.isEmpty(), `is`(false))
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun observeTask_givenValidTaskKey_returnsSuccess() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        val tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))

        val validTaskKey = TaskKey(plantOne.id, TaskType.WATER)
        val result = localDataSource.observeTask(validTaskKey)
        assertThat(result.getOrAwaitValue().succeeded, `is`(true))
        assertThat((result.value as Result.Success).data, `is`(plantOneWateringTaskDefaultSchedule))
    }

    @Test
    fun observeTask_givenInvalidTaskKey_returnsNull() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        val tasks = (localDataSource.getTasks() as Result.Success).data
        assertThat(tasks[0].key.plantId, `is`(1))

        val invalidTaskKey = TaskKey(plantOne.id, TaskType.PRUNE)
        val result = localDataSource.observeTask(invalidTaskKey)
        assertThat(result.getOrAwaitValue().succeeded, `is`(false))
        assertThat((result.value as Result.Error).exception.message, `is`(context.getString(R.string.test_error_not_found)))
    }

    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun observeTasks_givenEmptyTable_returnsEmptyList() = runBlocking {
        val result = localDataSource.observeActiveTasks()

        assertThat(result.getOrAwaitValue().succeeded, `is`(true))
        val tasks = (result.value as Result.Success).data
        assertThat(tasks.isEmpty(), `is`(true))
    }

    @Test
    fun observeTasks_givenNonEmptyTable_returnsList() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        localDataSource.saveTask(plantOnePruningTaskDefaultSchedule)

        val result = localDataSource.observeActiveTasks()

        assertThat(result.getOrAwaitValue().succeeded, `is`(true))
        val tasks = (result.value as Result.Success).data
        assertThat(tasks.size, `is`(2))
        assertThat(tasks[0].plantName, `is`(plantOne.commonName))
        assertThat(tasks[0].task.key, `is`(plantOneWateringTaskDefaultSchedule.key))
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun getTasks_givenEmptyTable_returnsEmptyList() = runBlocking {
        val result = localDataSource.getTasks()

        assertThat(result.succeeded, `is`(true))
        val tasks = (result as Result.Success).data
        assertThat(tasks.isEmpty(), `is`(true))
    }

    @Test
    fun getTasks_givenNonEmptyTable_returnsList() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.saveTask(plantOneWateringTaskDefaultSchedule)
        localDataSource.saveTask(plantOnePruningTaskDefaultSchedule)

        val result = localDataSource.getTasks()

        assertThat(result.succeeded, `is`(true))
        val tasks = (result as Result.Success).data
        assertThat(tasks.size, `is`(2))
    }

}