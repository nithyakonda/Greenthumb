package com.nkonda.greenthumb.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nkonda.greenthumb.data.testdoubles.FakeRepository
import com.nkonda.greenthumb.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {
    private lateinit var homeViewModel: HomeViewModel
    private val repository = FakeRepository()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        homeViewModel = HomeViewModel(repository)
    }

    @After
    fun teardown() {
        repository.setReturnError(false)
    }

    @Test
    fun markCompleted_givenValidTaskKey_returnsSuccess() {

    }

    @Test
    fun markCompleted_givenInvalidTaskKey_returnsError() {

    }

    @Test
    fun markCompleted_givenValidTaskKey_whenDbError_returnsError() {

    }
}