package com.nkonda.greenthumb.ui.home

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.*
import com.nkonda.greenthumb.data.source.IRepository
import kotlinx.coroutines.launch
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
    val tasks: LiveData<Result<List<TaskWithPlant>>> = repository.observeTasks().map { result ->
        if (result.succeeded)  {
            result as Result.Success
            if (result.data.isEmpty()) {
                Result.Error(Exception(ErrorCode.NO_ACTIVE_TASKS.code))
            } else {
                result
            }
        } else {
            result
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