package jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.model.GitHubRepo
import jp.ac.jec.cm0199.amazonqdeveloperingithubdemo.repository.GitHubRepository
import kotlinx.coroutines.launch

class GitHubViewModel(
    private val repository: GitHubRepository = GitHubRepository()
) : ViewModel() {
    
    private val _uiState = MutableLiveData<UiState>(UiState.Initial)
    val uiState: LiveData<UiState> = _uiState
    
    private var currentQuery = ""
    private var currentSort = "stars"
    private var currentOrder = "desc"
    
    fun searchRepositories(query: String, sort: String? = currentSort, order: String? = currentOrder) {
        if (query.isBlank()) return
        
        currentQuery = query
        currentSort = sort ?: "stars"
        currentOrder = order ?: "desc"
        
        _uiState.value = UiState.Loading
        
        viewModelScope.launch {
            val result = repository.searchRepositories(
                query = query,
                sort = sort,
                order = order
            )
            
            result.fold(
                onSuccess = { response ->
                    if (response.items.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(response.items)
                    }
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
    
    fun retry() {
        searchRepositories(currentQuery, currentSort, currentOrder)
    }
    
    fun updateSorting(sort: String, order: String) {
        currentSort = sort
        currentOrder = order
        if (currentQuery.isNotBlank()) {
            searchRepositories(currentQuery, sort, order)
        }
    }
    
    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        object Empty : UiState()
        data class Success(val repositories: List<GitHubRepo>) : UiState()
        data class Error(val message: String) : UiState()
    }
}