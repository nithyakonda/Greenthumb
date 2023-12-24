package com.nkonda.greenthumb.ui.search

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.data.succeeded
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(private val repository: IRepository) : ViewModel() {

    /**
     * Results
     */
    private val _searchResult = MutableLiveData<Result<List<PlantSummary>?>>()
    val searchResult:LiveData<Result<List<PlantSummary>?>> = _searchResult

    /**
     * Messages
     */
    private val _successMessage = MutableLiveData<String>()
    val successMessage = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    /**
     * Navigation
     */
    private val _navigateToSelectedPlant = MutableLiveData<Long>()
    val navigateToSelectedPlant:LiveData<Long> = _navigateToSelectedPlant

    fun searchPlantByName(name: String) {
        _searchResult.value = Result.Loading
        viewModelScope.launch {
            val result = repository.searchPlantByName(name)
            _searchResult.value = result
            if(result.succeeded) {
                _successMessage.value = "Found ${(result as Result.Success).data.size} results"
            } else {
                _errorMessage.value = (result as Result.Error).exception.message
                Timber.e((result as Result.Error).exception.message)
            }
        }
    }

    fun displayPlantDetails(plantId: Long) {
        _navigateToSelectedPlant.value = plantId
    }

    fun displayPlantDetailsComplete() {
        _navigateToSelectedPlant.value = -1L
    }
}