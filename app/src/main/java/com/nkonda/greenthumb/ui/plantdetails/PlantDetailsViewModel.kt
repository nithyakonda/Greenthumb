package com.nkonda.greenthumb.ui.plantdetails

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.source.IRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class PlantDetailsViewModel(private val repository: IRepository) : ViewModel() {

    /**
     * Results
     */
    private val _getPlantResult = MutableLiveData<Result<Plant?>>()
    val getPlantResult: LiveData<Result<Plant?>> = _getPlantResult

    /**
     * Observed Data
     * */
    // 1. this need not be an observable but a local state
    private val _isPlantSaved = MutableLiveData<Boolean>()
    val isPlantSaved: LiveData<Boolean> = _isPlantSaved

    private val _taskKey = MutableLiveData<TaskKey>()
    private val _currentTask = _taskKey.switchMap {
        repository.observeTask(it)
    }
    val currentTask: LiveData<Result<Task?>> = _currentTask

    /**
     * Messages
     */
    private val _progressIndicator = MutableLiveData<Boolean>()
    val progressIndicator: LiveData<Boolean> = _progressIndicator

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage = _successMessage

    fun getPlant(plantId: Long) {
        _getPlantResult.value = Result.Loading
        viewModelScope.launch {
            val (result, saved) = repository.getPlantById(plantId)
            _getPlantResult.value = result
            if(result.succeeded) {
                _isPlantSaved.value = saved
            } else {
                Timber.e(_errorMessage.value)
                _errorMessage.value = (result as Result.Error).exception.message
            }
        }
    }

    fun savePlant(plant: Plant) {
        _progressIndicator.value = true
        viewModelScope.launch {
            val result = repository.savePlant(plant)
            if (result.succeeded) {
                _successMessage.value = "Saved"
                _isPlantSaved.value = true
            } else {
                _errorMessage.value = (result as Result.Error).exception.message
                _isPlantSaved.value = false
            }
            _progressIndicator.value = false
        }
    }

    fun deletePlant(plant: Plant) {
        _progressIndicator.value = true
        viewModelScope.launch {
            val result = repository.deletePlant(plant.id)
            if (result.succeeded) {
                _successMessage.value = "Deleted"
                _isPlantSaved.value = false
            } else {
                _errorMessage.value = (result as Result.Error).exception.message
                _isPlantSaved.value = true
            }
            _progressIndicator.value = false
        }
    }

    /*----------------------------------------------------------------------------------------*/

    fun setCurrentTask(taskKey: TaskKey) {
        _taskKey.value = taskKey
    }

    fun saveTask(taskKey: TaskKey, expectedSchedule: Schedule) {
        _progressIndicator.value = true
        viewModelScope.launch {
            val task = Task(taskKey).also {
                it.schedule = expectedSchedule
            }
            val result = repository.saveTask(task)
            if (result.succeeded) {
                _successMessage.value = "Task Created"
            } else {
                _errorMessage.value = (result as Result.Error).exception.message
            }
            _progressIndicator.value = false
        }
    }

    fun deleteTask(taskKey: TaskKey) {
        _progressIndicator.value = true
        viewModelScope.launch {
            val result = repository.deleteTask(taskKey)
            if (result.succeeded) {
                _successMessage.value = "Task Deleted"
            } else {
                _errorMessage.value = (result as Result.Error).exception.message
            }
            _progressIndicator.value = false
        }
    }

    fun updateSchedule(taskKey: TaskKey, newSchedule: Schedule) {
        _progressIndicator.value = true
        viewModelScope.launch {
            val result = repository.updateSchedule(taskKey, newSchedule)
            if (result.succeeded) {
                _successMessage.value = "Task Schedule Updated"
            } else {
                _errorMessage.value = (result as Result.Error).exception.message
            }
            _progressIndicator.value = false
        }
    }
}