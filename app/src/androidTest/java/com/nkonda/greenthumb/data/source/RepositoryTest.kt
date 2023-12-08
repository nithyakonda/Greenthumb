package com.nkonda.greenthumb.data.source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest

import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.testdoubles.FakeLocalDataSource
import com.nkonda.greenthumb.data.source.testdoubles.FakeRemoteDataSource
import com.nkonda.greenthumb.data.succeeded
import com.nkonda.greenthumb.util.MainCoroutineRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @Before
    fun setup() {
        remoteDataSource = FakeRemoteDataSource()
        localDataSource = FakeLocalDataSource()

        repository = Repository(remoteDataSource, localDataSource, Dispatchers.Main)
    }

    @After
    fun teardown(){
        remoteDataSource.setReturnError(false)
    }


    @Test
    fun savePlant_whenDbError_returnsError() {

    }

    @Test
    fun deletePlantById_whenDbError_throwsException() {

    }

    @Test
    fun getPlantById_returnsPlantDetails() =  mainCoroutineRule.runBlockingTest{
        // When requested for a plant details with id which is not locally present
        val (plantDetails, saved) = repository.getPlantById(1)
        // Then plant details are fetched from the remote API
        assertThat(plantDetails?.succeeded, `is`(true))
        assertThat(saved, `is`(false))
    }

    @Test
    fun getPlantById_returnsNotFound() =  mainCoroutineRule.runBlockingTest{
        remoteDataSource.setReturnError(true)
        // When requested for a plant details with invalid id which is not locally present
        val (plantDetails, saved) = repository.getPlantById(1)
        // Then not found error is returned
        assertThat(plantDetails?.succeeded, `is`(false))
        assertThat(saved, `is`(false))
    }

    @Test
    fun getPlantById_tbd() {
        // Given plant id 1 is saved in db

        // When navigating from search screen to plant details

        // The FAB should show delete icon
    }

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
        assertThat(searchResult.exception.message, `is`("Network error"))
    }
}