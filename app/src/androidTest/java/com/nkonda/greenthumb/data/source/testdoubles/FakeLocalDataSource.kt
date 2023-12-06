package com.nkonda.greenthumb.data.source.testdoubles

import androidx.lifecycle.LiveData
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.local.ILocalDataSource

class FakeLocalDataSource : ILocalDataSource{

    override suspend fun savePlant(plant: Plant): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun observePlants(): LiveData<Result<List<Plant>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlants(): Result<List<Plant>> {
        TODO("Not yet implemented")
    }
}