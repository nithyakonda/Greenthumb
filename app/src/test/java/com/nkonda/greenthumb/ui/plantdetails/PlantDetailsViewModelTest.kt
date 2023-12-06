package com.nkonda.greenthumb.ui.plantdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.testdoubles.FakeRepository
import com.nkonda.greenthumb.util.MainCoroutineRule
import com.nkonda.greenthumb.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    fun getPlantById_loadingState(){
        mainCoroutineRule.pauseDispatcher()

        // When fetching plant details
        plantDetailsViewModel.getPlantById(1)

        // Then loading indicator is shown
        assertThat(plantDetailsViewModel.dataLoading.getOrAwaitValue(), `is`(true))

        // After the results are returned
        mainCoroutineRule.resumeDispatcher()

        // Then the loading indicator is hidden
        assertThat(plantDetailsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun getPlantById_returnsDetails(){
        // Given a valid plant Id
        // When get is called
        plantDetailsViewModel.getPlantById(1)

        // Then assert that plant details are returned for given id
        assertThat(plantDetailsViewModel.searchSuccess.getOrAwaitValue(), `is`(true))
        assertThat(plantDetailsViewModel.plant.getOrAwaitValue(), not(nullValue()))
        assertThat(plantDetailsViewModel.plant.getOrAwaitValue()?.id ?: 0, `is`(1))
    }

    @Test
    fun getPlantById_returnsError(){
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
    fun savePlant_Success() {
        // Given a plant not already present in the database
        val plant = Plant(2, "cName2", "sName2", "annual", "high", listOf("full_sun"), "high", listOf("April", "May"), "thumbnail2", "imageUrl2", "description2", null)

        // When save is success
        plantDetailsViewModel.savePlant(plant)

        // Then assert that successMessage is set correctly
        assertThat(plantDetailsViewModel.successMessage.getOrAwaitValue(), `is`("Saved"))
    }
}