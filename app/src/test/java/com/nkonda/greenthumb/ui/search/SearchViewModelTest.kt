package com.nkonda.greenthumb.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.succeeded
import com.nkonda.greenthumb.data.testdoubles.FakeRepository
import com.nkonda.greenthumb.util.MainCoroutineRule
import com.nkonda.greenthumb.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test

@ExperimentalCoroutinesApi
class SearchViewModelTest {
    private lateinit var searchViewModel: SearchViewModel
    private val repository = FakeRepository()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        searchViewModel = SearchViewModel(repository)
    }

    @After
    fun teardown() {
        repository.setReturnError(false)
    }

    @Test
    fun searchPlantByName_loadingState() {
        mainCoroutineRule.pauseDispatcher()

        // When searching with plant name
        searchViewModel.searchPlantByName("findOne")

        // Then loading indicator is shown
        assertThat(searchViewModel.searchResult.getOrAwaitValue(), `is`(Result.Loading))

        // After the results are returned
        mainCoroutineRule.resumeDispatcher()

        // Then the loading indicator is hidden
        assertThat(searchViewModel.searchResult.getOrAwaitValue(), `is`(not(Result.Loading)))
    }

    @Test
    fun searchPlantByName_returnsResults() {
        searchViewModel.apply {
            // Given a valid plant name with guaranteed results
            // When search is called
            searchPlantByName("findThree")

            // Then assert that non-zero result list is returned
            assertThat(searchResult.getOrAwaitValue().succeeded, `is`(true))
            val size = (searchResult.getOrAwaitValue() as Result.Success).data!!.size
            assertThat(size, `is`(3))
            assertThat(successMessage.getOrAwaitValue(), `is`("Found $size results"))
        }
    }

    @Test
    fun searchPlantByName_returnsEmptyResults() {
        searchViewModel.apply {
            // Given a valid plant name which has no results
            // When search is called
            searchPlantByName("findZero")

            // Then assert that zero result list is returned
            assertThat(searchResult.getOrAwaitValue().succeeded, `is`(true))
            val size = (searchResult.getOrAwaitValue() as Result.Success).data!!.size
            assertThat(size, `is`(0))
            assertThat(successMessage.getOrAwaitValue(), `is`("Found $size results"))
        }
    }

    @Test
    fun searchPlantByName_returnsError() {
        // Given a valid plant name with guaranteed results
        // When search is called in bad network state
        repository.setReturnError(true)
        searchViewModel.apply {
            searchPlantByName("findTwo")

            // Then assert that error is returned
            assertThat(searchResult.getOrAwaitValue().succeeded, `is`(false))
            assertThat(errorMessage.getOrAwaitValue(), `is`("Network error"))
        }
    }

    @Test
    fun searchPlantByName_returnsSuccessThenError() {
        // Given a valid plant name with guaranteed results
        searchViewModel.apply {
            searchPlantByName("findTwo")

            // Then assert that results are returned
            assertThat(searchResult.getOrAwaitValue().succeeded, `is`(true))
            var size = (searchResult.getOrAwaitValue() as Result.Success).data!!.size
            assertThat(size, `is`(2))
            assertThat(successMessage.getOrAwaitValue(), `is`("Found $size results"))

            // When another search is called in bad network state
            repository.setReturnError(true)
            searchViewModel.searchPlantByName("findTwo")

            assertThat(searchResult.getOrAwaitValue().succeeded, `is`(false))
            assertThat(searchViewModel.errorMessage.getOrAwaitValue(), `is`("Network error"))
        }
    }
}