package com.nkonda.greenthumb.ui.search

import android.util.Log
import androidx.lifecycle.*
import com.nkonda.greenthumb.data.Result
import com.nkonda.greenthumb.data.source.IRepository
import com.nkonda.greenthumb.data.source.remote.PlantSummary
import com.nkonda.greenthumb.data.succeeded
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchViewModel(private val repository: IRepository) : ViewModel() {
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _searchSuccess = MutableLiveData<Boolean>()
    val searchSuccess: LiveData<Boolean> = _searchSuccess

    private val _searchResults = MutableLiveData<List<PlantSummary>?>()
    val searchResults = _searchResults

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage = _errorMessage

    fun searchPlantByName(name: String) {
        _dataLoading.value = true
        viewModelScope.launch {
            val result = repository.searchPlantByName(name)
            if(result.succeeded) {
                _searchResults.value = (result as Result.Success).data
                _searchSuccess.value = true
            } else {
                _searchResults.value = emptyList()
                _errorMessage.value = (result as Result.Error).exception.message
                Timber.e((result as Result.Error).exception.message)
                _searchSuccess.value = false
            }
            _dataLoading.value = false
        }
    }
}

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory ( private val repository: IRepository ) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) = (SearchViewModel(repository) as T) }