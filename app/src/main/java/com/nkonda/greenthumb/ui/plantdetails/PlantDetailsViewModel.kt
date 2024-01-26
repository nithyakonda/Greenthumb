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
    private val _isPlantSaved = MutableLiveData<Boolean>()
    val isPlantSaved: LiveData<Boolean> = _isPlantSaved

    private val _taskSwitchState = MutableLiveData<Boolean>()
    val taskSwitchState: LiveData<Boolean> = _taskSwitchState

    private val _taskKey = MutableLiveData<TaskKey>()
    private val _currentTask = _taskKey.switchMap { taskKey ->
        repository.observeTask(taskKey).map { result ->
            if( result is Result.Success) {
                taskStateMap[taskKey]?.task = result.data!!
                _taskSwitchState.value = true
            } else {
                _taskSwitchState.value = false
            }
            result
        }
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

    /**
     * Local state
     */
    private val taskStateMap = mutableMapOf<TaskKey, TaskState>()

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
                if ((result as Result.Success).data == 1) {
                    _successMessage.value = "Deleted"
                } else {
                    _errorMessage.value = "Nothing to delete"
                }
                _isPlantSaved.value = false
            } else {
                _errorMessage.value = (result as Result.Error).exception.message
                _isPlantSaved.value = true
            }
            _progressIndicator.value = false
        }
    }

    /*----------------------------------------------------------------------------------------*/

    fun viewTask(task: Task) {
        taskStateMap[task.key] = TaskState(task)
        _taskKey.value = task.key
    }

    fun createTask(task: Task) {
        _progressIndicator.value = true
        taskStateMap[task.key]?.workflow = TaskWorkflow.Create

        viewModelScope.launch {
            val result = repository.saveTask(task)
            if (result.succeeded) {
                _successMessage.value = "Task Created"
            } else {
                _errorMessage.value = (result as Result.Error).exception.message
            }
            _progressIndicator.value = false
        }
    }

    fun updateTask(task: Task) {
        taskStateMap[task.key]?.workflow = TaskWorkflow.Update
    }

    fun cancelUpdate(taskKey: TaskKey) {
        if (taskStateMap[taskKey]?.workflow == TaskWorkflow.Create) {
            deleteTask(taskKey)
            _taskSwitchState.value = false
        } else if (taskStateMap[taskKey]?.workflow == TaskWorkflow.Update) {
            taskStateMap[taskKey]?.workflow = TaskWorkflow.View
        }
    }

    fun deleteTask(taskKey: TaskKey) {
        _progressIndicator.value = true
        _taskSwitchState.value = false
        taskStateMap[taskKey]?.workflow = TaskWorkflow.View
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
        taskStateMap[taskKey]?.workflow = TaskWorkflow.View
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

    fun getExistingSchedule(taskKey: TaskKey): Schedule? {
        return taskStateMap[taskKey]?.task?.schedule
    }

    private data class TaskState(var task: Task,
                                 var workflow: TaskWorkflow = TaskWorkflow.View
    )
}