package com.nkonda.greenthumb.ui.myplants

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.IRepository

class MyPlantsViewModel(private val repository: IRepository) : ViewModel() {

    /**
     * Results
     */
    private val _getPlantsResult = repository.observePlants()
    val getPlantsResult:LiveData<Result<List<Plant>>> = _getPlantsResult

    /**
     * Navigation
     */
    private val _navigateToSelectedPlant = MutableLiveData<Long>()
    val navigateToSelectedPlant:LiveData<Long> = _navigateToSelectedPlant

    fun displayPlantDetails(plantId: Long) {
        _navigateToSelectedPlant.value = plantId
    }

    fun displayPlantDetailsComplete() {
        _navigateToSelectedPlant.value = -1L
    }
}