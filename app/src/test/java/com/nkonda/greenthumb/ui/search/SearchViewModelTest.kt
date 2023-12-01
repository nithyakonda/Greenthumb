package com.nkonda.greenthumb.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nkonda.greenthumb.data.source.FakeRepository
import com.nkonda.greenthumb.util.MainCoroutineRule
import com.nkonda.greenthumb.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.CoreMatchers.`is`
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

    @Test
    fun searchPlantByName_loadingState() {
        mainCoroutineRule.pauseDispatcher()

        // When searching with plant name
        searchViewModel.searchPlantByName("findOne")

        // Then loading indicator is shown
        assertThat(searchViewModel.dataLoading.getOrAwaitValue(), `is`(true))

        // After the results are returned
        mainCoroutineRule.resumeDispatcher()

        // Then the loading indicator is hidden
        assertThat(searchViewModel.dataLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun searchPlantByName_returnsResults() {
        // Given a valid plant name with guaranteed results
        // When search is called
        searchViewModel.searchPlantByName("findThree")

        // Then assert that non-zero result list is returned
        assertThat(searchViewModel.searchSuccess.getOrAwaitValue(), `is`(true))
        assertThat(searchViewModel.searchResults.getOrAwaitValue()!!.size, `is`(3))
    }

    @Test
    fun searchPlantByName_returnsEmptyResults() {
        // Given a valid plant name which has no results
        // When search is called
        searchViewModel.searchPlantByName("findZero")

        // Then assert that zero result list is returned
        assertThat(searchViewModel.searchSuccess.getOrAwaitValue(), `is`(true))
        assertThat(searchViewModel.searchResults.getOrAwaitValue()!!.size, `is`(0))
    }

    @Test
    fun searchPlantByName_returnsError() {
        // Given a valid plant name with guaranteed results
        // When search is called in bad network state
        repository.setReturnError(true)
        searchViewModel.searchPlantByName("findTwo")

        // Then assert that error is returned
        assertThat(searchViewModel.searchSuccess.getOrAwaitValue(), `is`(false))
        assertThat(searchViewModel.errorMessage.getOrAwaitValue(), `is`("Network error"))
    }

    @Test
    fun searchPlantByName_returnsSuccessThenError() {
        // Given a valid plant name with guaranteed results
        searchViewModel.searchPlantByName("findTwo")

        // Then assert that results are returned
        assertThat(searchViewModel.searchSuccess.getOrAwaitValue(), `is`(true))
        assertThat(searchViewModel.searchResults.getOrAwaitValue()!!.size, `is`(2))

        // When another search is called in bad network state
        repository.setReturnError(true)
        searchViewModel.searchPlantByName("findTwo")

        assertThat(searchViewModel.searchSuccess.getOrAwaitValue(), `is`(false))
        assertThat(searchViewModel.searchResults.getOrAwaitValue()!!.size, `is`(0))
        assertThat(searchViewModel.errorMessage.getOrAwaitValue(), `is`("Network error"))    }
}