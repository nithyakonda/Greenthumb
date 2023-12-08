package com.nkonda.greenthumb.data.source.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.nkonda.greenthumb.R
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.Task
import com.nkonda.greenthumb.data.source.testdoubles.plantOne
import com.nkonda.greenthumb.data.source.testdoubles.plantTwo
import com.nkonda.greenthumb.data.source.testdoubles.taskOne
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
        assertThat(result.tasks?.size, `is`(0))

        plant.tasks?.add(taskOne)
        localDataSource.savePlant(plant)
        result = (localDataSource.getPlants() as Result.Success).data[0]
        assertThat(result.tasks?.size, `is`(1))
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
        assertThat(result.data, `is`(1))
        assertThat((localDataSource.getPlants() as Result.Success).data.size, `is`(1))

    }

    @Test
    fun deletePlantById_givenNonExistingPlant_returnsError() = runBlocking {
        localDataSource.savePlant(plantOne)
        localDataSource.savePlant(plantTwo)
        assertThat((localDataSource.getPlants() as Result.Success).data.size, `is`(2))

        val result = localDataSource.deletePlant(100)
        assertThat(result.succeeded, `is`(false))
        result as Result.Error
        assertThat(result.exception.message, `is`(context.getString(R.string.test_error_nothing_to_delete)))
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
}