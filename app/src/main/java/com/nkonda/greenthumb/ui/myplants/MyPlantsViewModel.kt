package com.nkonda.greenthumb.ui.myplants

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.IRepository

class MyPlantsViewModel(private val repository: IRepository) : ViewModel() {

    private val _searchResults = repository.observePlants()
    val searchResults:LiveData<Result<List<Plant>>> = _searchResults

    private val _navigateToSelectedPlant = MutableLiveData<Long>()
    val navigateToSelectedPlant:LiveData<Long> = _navigateToSelectedPlant


}