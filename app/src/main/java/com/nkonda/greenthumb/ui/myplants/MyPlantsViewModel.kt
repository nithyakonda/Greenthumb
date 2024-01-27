package com.nkonda.greenthumb.ui.myplants

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.source.IRepository
import java.lang.Exception

class MyPlantsViewModel(private val repository: IRepository) : ViewModel() {

    /**
     * Results
     */
    val getPlantsResult: LiveData<Result<List<Plant>>> = repository.observePlants().map { result ->
        if (result.succeeded) {
            result as Result.Success
            if (result.data.isEmpty()) {
                Result.Error(Exception(ErrorCode.NO_SAVED_PLANTS.code))
            } else {
                result
            }
        } else {
            result
        }
    }


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