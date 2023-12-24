package com.nkonda.greenthumb.ui.home

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.TaskKey
import com.nkonda.greenthumb.data.TaskWithPlant
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.succeeded
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: IRepository) : ViewModel() {
    /**
     * Messages
     */
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    /**
     * Observed Data
     */
    private val _tasks = repository.observeTasks()
    val tasks: LiveData<Result<List<TaskWithPlant>>> = _tasks

    fun markCompleted(taskKey: TaskKey, isCompleted: Boolean) {
        viewModelScope.launch {
            val result = repository.completeTask(taskKey, isCompleted)
            if (!result.succeeded) {
                _errorMessage.value = "Update complete failed"
            }
        }
    }
}