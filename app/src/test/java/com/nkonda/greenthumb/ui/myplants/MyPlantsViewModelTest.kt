package com.nkonda.greenthumb.ui.myplants

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nkonda.greenthumb.data.testdoubles.FakeRepository
import com.nkonda.greenthumb.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule

@ExperimentalCoroutinesApi
class MyPlantsViewModelTest {
    private lateinit var myPlantsViewModel: MyPlantsViewModel
    private val repository = FakeRepository()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        myPlantsViewModel = MyPlantsViewModel(repository)
    }

    @After
    fun teardown() {
        repository.setReturnError(false)
    }

}