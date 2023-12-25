package com.nkonda.greenthumb.ui.plantdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.TaskKey
import com.nkonda.greenthumb.data.TaskType
import com.nkonda.greenthumb.data.succeeded
import com.nkonda.greenthumb.data.testdoubles.*
import com.nkonda.greenthumb.util.MainCoroutineRule
import com.nkonda.greenthumb.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PlantDetailsViewModelTest {
    private lateinit var plantDetailsViewModel: PlantDetailsViewModel
    private val repository = FakeRepository()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        plantDetailsViewModel = PlantDetailsViewModel(repository)
    }

    @After
    fun teardown() {
        repository.setReturnError(false)
    }

    @Test
    fun getPlantById_verifyLoadingState(){
        mainCoroutineRule.pauseDispatcher()

        // When fetching plant details
        plantDetailsViewModel.getPlant(remotePlantOneId)

        // Then loading indicator is shown
        assertThat(plantDetailsViewModel.getPlantResult.getOrAwaitValue(), `is`(Result.Loading))

        // After the results are returned
        mainCoroutineRule.resumeDispatcher()

        // Then the loading indicator is hidden
        assertThat(plantDetailsViewModel.getPlantResult.getOrAwaitValue(), `is`(not(Result.Loading)))
    }

    @Test
    fun getPlantById_whenNotSaved_returnsDetailsFromNetwork(){
        plantDetailsViewModel.apply {
            // Given a valid plant Id
            // When get is called
            getPlant(remotePlantOneId)

            // Then assert that plant details are returned for given id
            assertThat(getPlantResult.getOrAwaitValue().succeeded, `is`(true))
            val plant = (getPlantResult.getOrAwaitValue() as Result.Success).data

            assertThat(plant, not(nullValue()))
            assertThat(plant?.id ?: 0, `is`(remotePlantOneId))
            assertThat(isPlantSaved.getOrAwaitValue(), `is`(false))
        }
    }

    @Test
    fun getPlantById_whenSaved_returnsDetailsFromDb(){
        repository.setGetFromDb(true)
        plantDetailsViewModel.apply {
            // Given a valid plant Id
            // When get is called
            getPlant(localPlantOneId)

            // Then assert that plant details are returned for given id
            assertThat(getPlantResult.getOrAwaitValue().succeeded, `is`(true))
            val plant = (getPlantResult.getOrAwaitValue() as Result.Success).data

            assertThat(plant, not(nullValue()))
            assertThat(plant?.id ?: 0, `is`(localPlantOneId))
            assertThat(isPlantSaved.getOrAwaitValue(), `is`(true))
        }
    }

    @Test
    fun getPlantById_whenBadNetwork_returnsError(){
        // Given bad network
        repository.setReturnError(true)
        plantDetailsViewModel.apply {
            // When get is called
            getPlant(1)

            // Then assert that error message is thrown
            assertThat(getPlantResult.getOrAwaitValue().succeeded, `is`(false))
            assertThat(errorMessage.getOrAwaitValue(), `is`("Network error"))
        }
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun savePlant_whenSuccess_showsSavedMessage() {
        plantDetailsViewModel.apply {
            // Given a plant not already present in the database
            // When save is success
            savePlant(plantTwo)

            // Then assert that successMessage is set correctly
            assertThat(successMessage.getOrAwaitValue(), `is`("Saved"))
            assertThat(isPlantSaved.value , `is`(true))
        }
    }

    @Test
    fun savePlant_whenDBError_showsErrorMessage() {
        repository.setReturnError(true)
        plantDetailsViewModel.apply {
            savePlant(plantOne)

            assertThat(errorMessage.getOrAwaitValue(), `is`("DB Error"))
            assertThat(isPlantSaved.value, `is`(false))
        }
    }

    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun deletePlant_givenValidPlant_returnsSuccess() = runBlocking {
        repository.savePlant(plantOne)
        plantDetailsViewModel.deletePlant(plantOne)

        assertThat(plantDetailsViewModel.isPlantSaved.getOrAwaitValue(), `is`(false))
        assertThat(plantDetailsViewModel.successMessage.getOrAwaitValue(), `is`("Deleted"))
    }

    @Test
    fun deletePlant_givenInvalidPlant_returnsError() = runBlocking {
        repository.savePlant(plantOne)
        plantDetailsViewModel.deletePlant(plantTwo)

        assertThat(plantDetailsViewModel.isPlantSaved.getOrAwaitValue(), `is`(false))
        assertThat(plantDetailsViewModel.errorMessage.getOrAwaitValue(), `is`("Nothing to delete"))
    }

    @Test
    fun deletePlant_whenDbError_returnsError() = runBlocking {
        repository.savePlant(plantOne)
        repository.setReturnError(true)
        plantDetailsViewModel.deletePlant(plantOne)

        assertThat(plantDetailsViewModel.isPlantSaved.getOrAwaitValue(), `is`(true))
        assertThat(plantDetailsViewModel.errorMessage.getOrAwaitValue(), `is`("DB Error"))
    }

    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun setCurrentTask_givenValidTaskKey_switchesToRequestedTask() {
        val validTaskKey = TaskKey(51L, TaskType.WATER)
        plantDetailsViewModel.setCurrentTask(validTaskKey)

        val result = plantDetailsViewModel.currentTask.getOrAwaitValue() as Result.Success
        assertThat(result.data, `is`(tasks[validTaskKey]))
    }

    @Test
    fun setCurrentTask_givenInvalidTaskKey_switchesToNullTask() {
        val invalidTaskKey = TaskKey(61L, TaskType.WATER)
        plantDetailsViewModel.setCurrentTask(invalidTaskKey)

        val result = plantDetailsViewModel.currentTask.getOrAwaitValue() as Result.Success
        assertThat(result.data, `is`(nullValue()))
    }

    @Test
    fun setCurrentTask_givenValidTaskKey_whenDbError_returnsError() {
        repository.setReturnError(true)
        val validTaskKey = TaskKey(51L, TaskType.WATER)
        plantDetailsViewModel.setCurrentTask(validTaskKey)

        val result = plantDetailsViewModel.currentTask.getOrAwaitValue() as Result.Error
        assertThat(result.exception.message, `is`("DB error"))
    }

    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun saveTask_whenSuccess_showsSuccessMessage() = runBlocking{
        repository.savePlant(plantOne)
        val taskKey = TaskKey(plantOne.id, TaskType.WATER)
        val schedule = plantOne.getExpectedSchedule(TaskType.WATER)
        plantDetailsViewModel.saveTask(taskKey, schedule)

        // Then assert that successMessage is set correctly
        assertThat(plantDetailsViewModel.successMessage.getOrAwaitValue(), `is`("Task Created"))
    }

    @Test
    fun saveTask_givenMismatchedScheduleAndTaskKey_showsError() {

    }

    @Test
    fun saveTask_whenDBError_showsErrorMessage() = runBlocking {
        repository.savePlant(plantOne)
        repository.setReturnError(true)
        val taskKey = TaskKey(plantOne.id, TaskType.WATER)
        val schedule = plantOne.getExpectedSchedule(TaskType.WATER)

        plantDetailsViewModel.saveTask(taskKey, schedule)

        assertThat(plantDetailsViewModel.errorMessage.getOrAwaitValue(), `is`("DB error"))

    }
    /*-------------------------------------------------------------------------------------------*/
    @Test
    fun deleteTask_givenValidTask_returnsSuccess() = runBlocking {
        repository.savePlant(plantOne)
        repository.saveTask(plantOneWateringTaskDefaultSchedule)
        val validTaskKey = TaskKey(plantOne.id, TaskType.WATER)

        plantDetailsViewModel.deleteTask(validTaskKey)

        assertThat(plantDetailsViewModel.successMessage.getOrAwaitValue(), `is`("Task Deleted"))
    }

    @Test
    fun deleteTask_givenInvalidTask_returnsError() = runBlocking {repository.savePlant(plantOne)
        repository.savePlant(plantOne)
        repository.saveTask(plantOneWateringTaskDefaultSchedule)
        val invalidTaskKey = TaskKey(plantOne.id, TaskType.PRUNE)

        plantDetailsViewModel.deleteTask(invalidTaskKey)

        assertThat(plantDetailsViewModel.errorMessage.getOrAwaitValue(), `is`("Nothing to delete"))
    }

    @Test
    fun deleteTask_whenDbError_returnsError() = runBlocking {
        repository.saveTask(plantOneWateringTaskDefaultSchedule)
        repository.setReturnError(true)

        plantDetailsViewModel.deleteTask(TaskKey(plantOne.id, TaskType.WATER))

        assertThat(plantDetailsViewModel.errorMessage.getOrAwaitValue(), `is`("DB error"))
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun updateSchedule_givenValidTaskKey_returnsSuccess() = runBlocking {
        repository.savePlant(plantOne)
        repository.saveTask(plantOneWateringTaskDefaultSchedule)
        val validTaskKey = TaskKey(plantOne.id, TaskType.WATER)

        plantDetailsViewModel.updateSchedule(validTaskKey, wateringOneExpectedSchedule)

        assertThat(plantDetailsViewModel.successMessage.getOrAwaitValue(), `is`("Task Schedule Updated"))

    }

    @Test
    fun updateSchedule_givenInvalidTaskKey_returnsError() = runBlocking {
        repository.savePlant(plantOne)
        repository.saveTask(plantOneWateringTaskDefaultSchedule)
        val invalidTaskKey = TaskKey(plantOne.id, TaskType.PRUNE)

        plantDetailsViewModel.updateSchedule(invalidTaskKey, wateringOneExpectedSchedule)

        assertThat(plantDetailsViewModel.errorMessage.getOrAwaitValue(), `is`("Nothing to update"))

    }

    @Test
    fun updateSchedule_givenValidTaskKey_whenDbError_returnsError() = runBlocking {
        repository.saveTask(plantOneWateringTaskDefaultSchedule)
        repository.saveTask(plantOneWateringTaskDefaultSchedule)
        val validTaskKey = TaskKey(plantOne.id, TaskType.WATER)
        repository.setReturnError(true)

        plantDetailsViewModel.updateSchedule(validTaskKey, wateringOneExpectedSchedule)

        assertThat(plantDetailsViewModel.errorMessage.getOrAwaitValue(), `is`("DB error"))

    }
    @Test
    fun getPlantById_tbd() {
        // Given plant id 1 is saved in db

        // When navigating from search screen to plant details

        // The FAB should show delete icon
    }
}