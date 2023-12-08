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

    private val _successMessage = MutableLiveData<String>()
    val successMessage = _successMessage

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> = _isSaved

    private val _plantDeleted = MutableLiveData<Boolean>()
    val plantDeleted: LiveData<Boolean> = _plantDeleted

    fun getPlantById(plantId: Long) {
        _dataLoading.value = true
        viewModelScope.launch {
            val (result, saved) = repository.getPlantById(plantId)
            if(result.succeeded) {
                _isSaved.value = saved
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

    fun savePlant(plant: Plant) {
        _dataLoading.value = true
        viewModelScope.launch {
            val result = repository.savePlant(plant)
            if (result.succeeded) {
                _successMessage.value = "Saved"
                _isSaved.value = true
            } else {
                _errorMessage.value = (result as Result.Error).exception.message
                _isSaved.value = false
            }
            _dataLoading.value = false
        }
    }

    fun deletePlant(plant: Plant) {

    }
}