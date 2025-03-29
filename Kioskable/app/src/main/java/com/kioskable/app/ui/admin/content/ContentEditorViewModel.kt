package com.kioskable.app.ui.admin.content

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kioskable.app.domain.model.Content
import com.kioskable.app.domain.model.Result
import com.kioskable.app.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentEditorViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Success(false))
    val uiState: StateFlow<UiState> = _uiState
    
    // Store content ID when editing
    var contentId: String? = null
    
    fun loadContent(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = contentRepository.getContentById(id)) {
                is Result.Success -> {
                    contentId = id
                    _uiState.value = UiState.ContentLoaded(result.data)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
            }
        }
    }
    
    fun saveContent(content: Content) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            // This is a simplified version. In a real app, you would have:
            // 1. For new content: API call to create new content
            // 2. For existing content: API call to update existing content
            // For now, we'll simulate a successful save
            
            // Simulating a network delay
            kotlinx.coroutines.delay(1000)
            
            // Simulate success
            _uiState.value = UiState.Success(true)
        }
    }
    
    sealed class UiState {
        object Loading : UiState()
        data class Success(val finished: Boolean) : UiState()
        data class ContentLoaded(val content: Content) : UiState()
        data class Error(val message: String) : UiState()
    }
} 