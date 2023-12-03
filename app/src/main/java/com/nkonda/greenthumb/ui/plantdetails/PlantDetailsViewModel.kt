package com.nkonda.greenthumb.ui.plantdetails

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.Plant
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.succeeded
import kotlinx.coroutines.launch
import timber.log.Timber

class PlantDetailsViewModel(private val repository: IRepository) : ViewModel() {
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _searchSuccess = MutableLiveData<Boolean>()
    val searchSuccess: LiveData<Boolean> = _searchSuccess

    private var _plant = MutableLiveData<Plant?>()
    val plant:LiveData<Plant?> = _plant

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage = _errorMessage

    fun getPlantById(plantId: Long) {
        _dataLoading.value = true
        viewModelScope.launch {
            val result = repository.getPlantById(plantId)
            if(result.succeeded) {
                _plant.value = (result as Result.Success).data
                _searchSuccess.value = true
            } else {
                Timber.e(_errorMessage.value)
                _errorMessage.value = (result as Result.Error).exception.message
                _plant.value = null
                _searchSuccess.value = false
            }
            _dataLoading.value = false
        }
    }
}