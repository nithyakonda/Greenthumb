package com.nkonda.greenthumb.ui.home

import androidx.lifecycle.*
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.TaskKey
import com.nkonda.greenthumb.data.TaskWithPlant
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.source.Repository
import com.nkonda.greenthumb.data.succeeded
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: IRepository) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _tasks = repository.observeTasks()
    val tasks: LiveData<Result<List<TaskWithPlant>>> = _tasks

    fun markCompleted(taskKey: TaskKey, isCompleted: Boolean) {
        viewModelScope.launch {
            val result = repository.completeTask(taskKey, isCompleted)
            if (!result.succeeded) {
                _message.value = "Update complete failed"
            }
        }
    }
}