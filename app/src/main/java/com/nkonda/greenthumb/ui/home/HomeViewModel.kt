package com.nkonda.greenthumb.ui.home

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.source.IRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class HomeViewModel(private val repository: IRepository) : ViewModel() {
    /**
     * Messages
     */
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    /**
     * Observed Data
     */
    private val _tasks = MediatorLiveData<Result<List<TaskWithPlant>>>()
    val tasks: LiveData<Result<List<TaskWithPlant>>> get() = _tasks
//   todo This should ideally work, using refresh() until then
//    val tasks: LiveData<Result<List<TaskWithPlant>>> = repository.observeActiveTasks().map { result ->
//        if (result.succeeded)  {
//            result as Result.Success
//            if (result.data.isEmpty()) {
//                Result.Error(Exception(ErrorCode.NO_ACTIVE_TASKS.code))
//            } else {
//                result
//            }
//        } else {
//            result
//        }
//    }

    override fun onCleared() {
        super.onCleared()
    }

    fun refresh() {
        // Hack to refresh tasks, since for some reason tasks that are deleted because of CASCADE policy when plant is deleted is not updating the observer.
        _tasks.removeSource(tasks)
        _tasks.addSource(repository.observeActiveTasks()) { result ->
            val refreshedResult = if (result.succeeded) {
                result as Result.Success
                if ( result.data.isEmpty()) {
                    Result.Error(Exception(ErrorCode.NO_ACTIVE_TASKS.code))
                } else {
                    result
                }
            } else {
                result
            }
            _tasks.value = refreshedResult
        }
    }

    fun markCompleted(taskKey: TaskKey, isCompleted: Boolean) {
        viewModelScope.launch {
            val result = repository.completeTask(taskKey, isCompleted)
            if (!result.succeeded) {
                _errorMessage.value = "Update complete failed"
            }
        }
    }
}