package com.nkonda.greenthumb.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.TaskKey
import com.nkonda.greenthumb.data.TaskWithPlant
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.source.Repository

class HomeViewModel(private val repository: IRepository) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _tasks = repository.observeTasks()
    val tasks: LiveData<Result<List<TaskWithPlant>>> = _tasks

    fun markCompleted(taskKey: TaskKey) {

    }

    private fun processTasks(tasksResult: Result<List<TaskWithPlant>>): MutableLiveData<List<TaskWithPlant>> {
        val result = MutableLiveData<List<TaskWithPlant>>()
        when (tasksResult) {
            is Result.Success -> {
                _dataLoading.value = false
                result.value = tasksResult.data!!
                _message.value = " Found ${tasksResult.data?.size} tasks"
            }
            is Result.Error -> {
                _dataLoading.value = false
                result.value = emptyList()
                _message.value = " Error "
            }
            Result.Loading -> _dataLoading.value = true
        }
        return result
    }
}