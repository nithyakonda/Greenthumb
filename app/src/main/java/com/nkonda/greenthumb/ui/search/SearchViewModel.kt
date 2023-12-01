package com.nkonda.greenthumb.ui.search

import android.util.Log
import androidx.lifecycle.*
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.data.succeeded
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(private val repository: IRepository) : ViewModel() {
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _searchSuccess = MutableLiveData<Boolean>()
    val searchSuccess: LiveData<Boolean> = _searchSuccess

    private val _searchResults = MutableLiveData<List<PlantSummary>?>()
    val searchResults:LiveData<List<PlantSummary>?> = _searchResults

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _navigateToSelectedPlant = MutableLiveData<Long>()
    val navigateToSelectedPlant:LiveData<Long> = _navigateToSelectedPlant

    fun searchPlantByName(name: String) {
        _dataLoading.value = true
        viewModelScope.launch {
            val result = repository.searchPlantByName(name)
            if(result.succeeded) {
                _searchResults.value = (result as Result.Success).data
                _searchSuccess.value = true
            } else {
                _searchResults.value = emptyList()
                _errorMessage.value = (result as Result.Error).exception.message
                Timber.e((result as Result.Error).exception.message)
                _searchSuccess.value = false
            }
            _dataLoading.value = false
        }
    }

    fun displayPlantDetails(plantId: Long) {
        _navigateToSelectedPlant.value = plantId
    }

    fun displayPlantDetailsComplete() {
        _navigateToSelectedPlant.value = -1L
    }
}