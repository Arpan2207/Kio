package com.kioskable.app.ui.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kioskable.app.domain.model.Content
import com.kioskable.app.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentDisplayViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ContentDisplayUiState>(ContentDisplayUiState.Loading)
    val uiState: StateFlow<ContentDisplayUiState> = _uiState
    
    private var contentRotationJob: Job? = null
    private var currentContentIndex = 0
    private var contentList = listOf<Content>()
    
    init {
        fetchContent()
    }
    
    private fun fetchContent() {
        viewModelScope.launch {
            contentRepository.observeDisplayContent()
                .catch { 
                    _uiState.value = ContentDisplayUiState.Error("Failed to load content: ${it.message}")
                }
                .collectLatest { content ->
                    contentList = content
                    
                    if (contentList.isEmpty()) {
                        _uiState.value = ContentDisplayUiState.Empty
                    } else {
                        startContentRotation()
                    }
                }
        }
    }
    
    private fun startContentRotation() {
        contentRotationJob?.cancel()
        currentContentIndex = 0
        
        if (contentList.isEmpty()) {
            _uiState.value = ContentDisplayUiState.Empty
            return
        }
        
        contentRotationJob = viewModelScope.launch {
            while (true) {
                val currentContent = contentList[currentContentIndex]
                _uiState.value = ContentDisplayUiState.Success(currentContent)
                
                // Determine how long to display this content
                val duration = currentContent.duration?.toLong() ?: DEFAULT_DISPLAY_DURATION
                delay(duration * 1000)
                
                // Move to next content
                currentContentIndex = (currentContentIndex + 1) % contentList.size
            }
        }
    }
    
    fun loadContentForPreview(contentId: String) {
        viewModelScope.launch {
            _uiState.value = ContentDisplayUiState.Loading
            
            when (val result = contentRepository.getContentById(contentId)) {
                is Result.Success -> {
                    // Set directly to display this single content
                    _uiState.value = ContentDisplayUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = ContentDisplayUiState.Error("Failed to load preview: ${result.message}")
                }
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        contentRotationJob?.cancel()
    }
    
    companion object {
        const val DEFAULT_DISPLAY_DURATION = 10L // 10 seconds default
    }
}

sealed class ContentDisplayUiState {
    object Loading : ContentDisplayUiState()
    object Empty : ContentDisplayUiState()
    data class Success(val content: Content) : ContentDisplayUiState()
    data class Error(val message: String) : ContentDisplayUiState()
} 