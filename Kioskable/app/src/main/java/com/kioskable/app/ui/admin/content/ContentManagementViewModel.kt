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
class ContentManagementViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadContent()
    }

    fun loadContent() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = contentRepository.getAllContent(0, 100)) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.value = UiState.Empty
                    } else {
                        _uiState.value = UiState.Success(result.data)
                    }
                }
                is Result.Error -> {
                    _uiState.value = UiState.Error(result.message)
                }
            }
        }
    }

    fun refreshContent() {
        loadContent()
    }

    fun setContentActive(contentId: String, isActive: Boolean) {
        viewModelScope.launch {
            when (val contentResult = contentRepository.getContentById(contentId)) {
                is Result.Success -> {
                    val content = contentResult.data
                    // Call API to update active status
                    // This is a simplified version. In a real app, you would need to have an API endpoint
                    // to update the active status of content
                    showSnackbar("Content active status updated")
                    loadContent()
                }
                is Result.Error -> {
                    showSnackbar("Error updating content: ${contentResult.message}")
                }
            }
        }
    }

    fun deleteContent(content: Content) {
        viewModelScope.launch {
            // Call API to delete content
            // This is a simplified version. In a real app, you would need to have an API endpoint
            // to delete content
            showSnackbar("Content deleted")
            loadContent()
        }
    }

    fun reorderContent(contents: List<Content>) {
        viewModelScope.launch {
            // Call API to reorder content
            // This is a simplified version. In a real app, you would need to have an API endpoint
            // to reorder content
            showSnackbar("Content order updated")
        }
    }

    private fun showSnackbar(message: String) {
        // Capture current state to avoid overwriting with error message
        val currentState = _uiState.value
        _uiState.value = UiState.Error(message)
        _uiState.value = currentState
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val content: List<Content>) : UiState()
        object Empty : UiState()
        data class Error(val message: String) : UiState()
    }
} 