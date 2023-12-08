package com.nkonda.greenthumb.ui.plantdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nkonda.greenthumb.data.source.testdoubles.FakeRepository
import com.nkonda.greenthumb.data.testdoubles.localPlantOneId
import com.nkonda.greenthumb.data.testdoubles.plantOne
import com.nkonda.greenthumb.data.testdoubles.plantTwo
import com.nkonda.greenthumb.data.testdoubles.remotePlantOneId
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
        plantDetailsViewModel.getPlantById(remotePlantOneId)

        // Then loading indicator is shown
        assertThat(plantDetailsViewModel.dataLoading.getOrAwaitValue(), `is`(true))

        // After the results are returned
        mainCoroutineRule.resumeDispatcher()

        // Then the loading indicator is hidden
        assertThat(plantDetailsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun getPlantById_whenNotSaved_returnsDetailsFromNetwork(){
        // Given a valid plant Id
        // When get is called
        plantDetailsViewModel.getPlantById(remotePlantOneId)

        // Then assert that plant details are returned for given id
        plantDetailsViewModel.apply {
            assertThat(searchSuccess.getOrAwaitValue(), `is`(true))
            assertThat(plant.getOrAwaitValue(), not(nullValue()))
            assertThat(plant.getOrAwaitValue()?.id ?: 0, `is`(remotePlantOneId))
            assertThat(isSaved.getOrAwaitValue(), `is`(false))
        }
    }

    @Test
    fun getPlantById_whenSaved_returnsDetailsFromDb(){
        repository.setGetFromDb(true)
        // Given a valid plant Id
        // When get is called
        plantDetailsViewModel.getPlantById(localPlantOneId)

        // Then assert that plant details are returned for given id
        plantDetailsViewModel.apply {
            assertThat(searchSuccess.getOrAwaitValue(), `is`(true))
            assertThat(plant.getOrAwaitValue(), not(nullValue()))
            assertThat(plant.getOrAwaitValue()?.id ?: 0, `is`(localPlantOneId))
            assertThat(isSaved.getOrAwaitValue(), `is`(true))
        }
    }

    @Test
    fun getPlantById_whenBadNetwork_returnsError(){
        // Given bad network
        repository.setReturnError(true)
        // When get is called
        plantDetailsViewModel.getPlantById(1)

        // Then assert that error message is thrown
        assertThat(plantDetailsViewModel.searchSuccess.getOrAwaitValue(), `is`(false))
        assertThat(plantDetailsViewModel.plant.getOrAwaitValue(), `is`(nullValue()))
        assertThat(plantDetailsViewModel.errorMessage.getOrAwaitValue(), `is`("Network error"))
    }

    @Test
    fun savePlant_whenSuccess_showsSavedMessage() {
        // Given a plant not already present in the database
        // When save is success
        plantDetailsViewModel.savePlant(plantTwo)

        // Then assert that successMessage is set correctly
        assertThat(plantDetailsViewModel.successMessage.getOrAwaitValue(), `is`("Saved"))
    }

    @Test
    fun savePlant_whenDBError_showsErrorMessage() {
        repository.setReturnError(true)
        plantDetailsViewModel.savePlant(plantOne)

        assertThat(plantDetailsViewModel.errorMessage.getOrAwaitValue(), `is`("DB Error"))
    }

    /*-------------------------------------------------------------------------------------------*/

    @Test
    fun deletePlant_whenSuccess() = runBlocking {
        repository.savePlant(plantOne)
        plantDetailsViewModel.deletePlant(plantOne)
        assertThat(plantDetailsViewModel.isSaved.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun deletePlant_whenError() = runBlocking {
        repository.savePlant(plantOne)
        repository.setReturnError(true)
        plantDetailsViewModel.deletePlant(plantOne)
        assertThat(plantDetailsViewModel.isSaved.getOrAwaitValue(), `is`(true))
    }

    /*-------------------------------------------------------------------------------------------*/


    @Test
    fun getPlantById_tbd() {
        // Given plant id 1 is saved in db

        // When navigating from search screen to plant details

        // The FAB should show delete icon
    }
}