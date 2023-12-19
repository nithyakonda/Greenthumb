package com.nkonda.greenthumb.ui.plantdetails

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.source.IRepository
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

    private val _deletePlantResult = MutableLiveData<Result<Unit>>()
    val deletePlantResult: LiveData<Result<Unit>> = _deletePlantResult

    private val _deleteTaskResult = MutableLiveData<Result<Unit>>()
    val deleteTaskResult: LiveData<Result<Unit>> = _deleteTaskResult

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

    private lateinit var tasksMap:MutableMap<TaskType, Task>

    fun loadData(plantId: Long) {
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
            tasksMap = repository.getUniqueTasks(plantId) as MutableMap<TaskType, Task>
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
        _deletePlantResult.value = Result.Loading
        viewModelScope.launch {
            val result = repository.deletePlant(plant.id)
            _deletePlantResult.value = result
            if (result.succeeded) {
                _successMessage.value = "Deleted"
                _isSaved.value = false
            } else {
                _errorMessage.value = (result as Result.Error).exception.message
                _isSaved.value = true
            }
        }
    }

    /*----------------------------------------------------------------------------------------*/

    fun saveTask() {
        _task.value?.let { currentTask ->
            _dataLoading.value = true
            viewModelScope.launch {
                val result = repository.saveTask(currentTask)
                if (result.succeeded) {
                    _task.value = currentTask
                    tasksMap[currentTask.key.taskType] = currentTask
                    _successMessage.value = "Task Created"
                } else {
                    _errorMessage.value = (result as Result.Error).exception.message
                }
                _dataLoading.value = false
            }
        } ?: Timber.w("Save task failed") // todo add more error handling
    }

    fun deleteTask() {
        _task.value?.let { currentTask ->
            _deleteTaskResult.value = Result.Loading
            viewModelScope.launch {
                val result = repository.deleteTask(currentTask.key)
                _deleteTaskResult.value = result
                if (result.succeeded) {
                    Task.getDefaultTask(currentTask.key).also {
                        _task.value = it
                        tasksMap[currentTask.key.taskType] = it
                    }
                }
            }
        }
    }

    fun getTask(taskType: TaskType): Task {
        _task.value = tasksMap[taskType]
        return _task.value!!
    }

    fun updateSchedule(newSchedule: Schedule) {
        _task.value?.let { currentTask ->
            currentTask.copy(schedule = newSchedule).also {
                _task.value = it
                tasksMap[currentTask.key.taskType] = it
            }
        } ?: Timber.w("Update schedule failed") // todo add more error handling
    }
}