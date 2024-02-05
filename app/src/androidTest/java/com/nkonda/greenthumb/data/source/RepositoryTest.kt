package com.nkonda.greenthumb.data.source

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.nkonda.greenthumb.R

import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.TaskKey
import com.nkonda.greenthumb.data.TaskType
import com.nkonda.greenthumb.data.source.testdoubles.*
import com.nkonda.greenthumb.data.succeeded
import com.nkonda.greenthumb.util.MainCoroutineRule
import com.nkonda.greenthumb.util.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RepositoryTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remoteDataSource: FakeRemoteDataSource
    private lateinit var localDataSource: FakeLocalDataSource

    // Class under test
    private lateinit var repository: Repository
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        remoteDataSource = FakeRemoteDataSource()
        localDataSource = FakeLocalDataSource()

        repository = Repository(remoteDataSource, localDataSource, Dispatchers.Main)
    }

    @After
    fun teardown() {
        localDataSource.setReturnError(false)
        remoteDataSource.setReturnError(false)
    }

    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun savePlant_whenDbError_returnsError() = runBlocking {
        localDataSource.setReturnError(true)
        val result = repository.savePlant(plantOne)
        assertDbError(result)
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun deletePlantById_givenValidPlantId_returns1() = runBlocking {
        repository.savePlant(plantOne)
        repository.savePlant(plantTwo)
        val result = repository.deletePlant(plantOne.id)
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data, `is`(1))
    }

    @Test
    fun deletePlantById_whenNothingToDelete_returns0() = runBlocking {
        repository.savePlant(plantOne)
        val result = repository.deletePlant(plantTwo.id)
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data, `is`(0))
    }

    @Test
    fun deletePlantById_whenDbError_throwsException() = runBlocking {
        repository.savePlant(plantOne)
        localDataSource.setReturnError(true)
        val result = repository.deletePlant(plantTwo.id)

        assertDbError(result)
    }

    private fun assertDbError(result: Result<*>) {
        assertThat(result.succeeded, `is`(false))
        result as Result.Error
        assertThat(result.exception.message, `is`(context.getString(R.string.test_error_db_error)))
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun observePlants_whenDbError_throwsException() {
        localDataSource.setReturnError(true)
        val result = repository.observePlants().getOrAwaitValue()
        assertDbError(result)
    }

    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun getPlants_whenDBError_returnsError() = runBlocking {
        localDataSource.setReturnError(true)
        val result = repository.getPlants()
        assertDbError(result)
    }
    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun getPlantById_whenNotSaved_returnsDataFromNetwork() = mainCoroutineRule.runBlockingTest {
        // When requested for a plant details with id which is not locally present
        val (plantDetails, saved) = repository.getPlantById(1)
        // Then plant details are fetched from the remote API
        assertThat(plantDetails?.succeeded, `is`(true))
        assertThat(saved, `is`(false))
    }

    @Test
    fun getPlantById_whenNotSaved_returnsNotFound() = mainCoroutineRule.runBlockingTest {
        remoteDataSource.setReturnError(true)
        // When requested for a plant details with invalid id which is not locally present
        val (plantDetails, saved) = repository.getPlantById(1)
        // Then not found error is returned
        assertThat(plantDetails?.succeeded, `is`(false))
        assertThat(saved, `is`(false))
    }

    @Test
    fun getPlantById_givenSavedId_returnsDataFromDB() = runBlocking {
        repository.savePlant(plantOne)
        val (plantDetails, saved) = repository.getPlantById(1)

        assertThat(plantDetails?.succeeded, `is`(true))
        assertThat(saved, `is`(true))
    }

    @Test
    fun getPlantById_givenSavedId_whenDbError_returnsDataFromNetwork() = runBlocking {
        repository.savePlant(plantOne)
        localDataSource.setReturnError(true)
        val (plantDetails, saved) = repository.getPlantById(1)

        assertThat(plantDetails?.succeeded, `is`(true))
        assertThat(saved, `is`(false))
    }


    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun searchPlantByName_findsMultipleSearchResults() = mainCoroutineRule.runBlockingTest {
        // When searching for a valid plant
        val searchResult = repository.searchPlantByName("findThree")!!

        // Then correct search results are returned
        assertThat(searchResult.succeeded, `is`(true))
        searchResult as Result.Success
        assertThat(searchResult.data!!.size, `is`(3))
    }

    @Test
    fun searchPlantByName_returnsEmptyList() = mainCoroutineRule.runBlockingTest {
        // When searching for a invalid plant
        val searchResult = repository.searchPlantByName("findZero")!!

        // Then not found is returned
        assertThat(searchResult.succeeded, `is`(true))
        searchResult as Result.Success
        assertThat(searchResult.data!!.size, `is`(0))
    }

    @Test
    fun searchPlantByName_returnsNetworkError() = mainCoroutineRule.runBlockingTest {
        // When searching for a valid plant but network is down
        remoteDataSource.setReturnError(true)
        val searchResult = repository.searchPlantByName("findOne")!!

        // Then network error is returned
        assertThat(searchResult.succeeded, `is`(false))
        searchResult as Result.Error
        assertThat(
            searchResult.exception.message,
            `is`(context.getString(R.string.test_error_network_error))
        )
    }

/*-------------------------------------------------------------------------------------------*/

    @Test
    fun saveTask_whenDBError_returnsError() = runBlocking {
        repository.savePlant(plantOne)
        localDataSource.setReturnError(true)
        val result = repository.saveTask(plantOneWateringTaskDefaultSchedule)
        assertDbError(result)
    }
/*-------------------------------------------------------------------------------------------*/

    @Test
    fun updateSchedule_whenDBError_returnsError() = runBlocking {
        repository.savePlant(plantOne)
        repository.saveTask(plantOneWateringTaskDefaultSchedule)
        localDataSource.setReturnError(true)
        val result = repository.updateSchedule(TaskKey(plantOne.id, TaskType.Water), wateringOneExpectedSchedule)
        assertDbError(result)
    }

    /*-------------------------------------------------------------------------------------------*/
    @Test
    fun updateCompleted_whenDBError_returnsError() = runBlocking {
        repository.savePlant(plantOne)
        repository.saveTask(plantOneWateringTaskDefaultSchedule)
        localDataSource.setReturnError(true)
        val result = repository.completeTask(TaskKey(plantOne.id, TaskType.Water), true)
        assertDbError(result)
    }
/*-------------------------------------------------------------------------------------------*/

    @Test
    fun deleteTask_whenDBError_returnsError() = runBlocking {
        repository.savePlant(plantOne)
        repository.saveTask(plantOneWateringTaskDefaultSchedule)
        localDataSource.setReturnError(true)
        val result = repository.deleteTask(TaskKey(plantOne.id, TaskType.Water))
        assertDbError(result)
    }
/*-------------------------------------------------------------------------------------------*/

    @Test
    fun observeTask_whenDBError_returnsError() = runBlocking {
        localDataSource.setReturnError(true)
        val result = repository.observeTask(TaskKey(plantOne.id, TaskType.Water)).getOrAwaitValue()
        assertDbError(result)
    }
/*-------------------------------------------------------------------------------------------*/

    @Test
    fun observeTasks_whenDBError_returnsError() = runBlocking {
        localDataSource.setReturnError(true)
        val result = repository.observeActiveTasks().getOrAwaitValue()
        assertDbError(result)
    }
/*-------------------------------------------------------------------------------------------*/
}



