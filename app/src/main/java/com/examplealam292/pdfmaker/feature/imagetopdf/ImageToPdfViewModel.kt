package com.examplealam292.pdfmaker.feature.imagetopdf

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examplealam292.pdfmaker.core.common.result.onError
import com.examplealam292.pdfmaker.core.common.result.onSuccess
import com.examplealam292.pdfmaker.domain.model.ImagePageItem
import com.examplealam292.pdfmaker.domain.model.ImageToPdfParams
import com.examplealam292.pdfmaker.domain.model.PageMargin
import com.examplealam292.pdfmaker.domain.model.PageOrientation
import com.examplealam292.pdfmaker.domain.model.PageSize
import com.examplealam292.pdfmaker.domain.usecase.CreatePdfFromImagesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImageToPdfViewModel(
    private val createPdfFromImagesUseCase: CreatePdfFromImagesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImageToPdfUiState())
    val uiState: StateFlow<ImageToPdfUiState> = _uiState.asStateFlow()

    fun onImagesSelected(uris: List<Uri>) {
        val newItems = uris.map { ImagePageItem(uri = it) }
        _uiState.update { it.copy(images = it.images + newItems) }
    }

    fun removeImage(index: Int) {
        _uiState.update { state ->
            val updated = state.images.toMutableList()
            if (index in updated.indices) {
                updated.removeAt(index)
            }
            state.copy(images = updated)
        }
    }

    fun rotateImage(index: Int) {
        _uiState.update { state ->
            val updated = state.images.toMutableList()
            if (index in updated.indices) {
                val current = updated[index]
                updated[index] = current.copy(rotationDegrees = (current.rotationDegrees + 90) % 360)
            }
            state.copy(images = updated)
        }
    }

    fun onFileNameChanged(name: String) {
        _uiState.update { it.copy(fileName = name) }
    }

    fun onPageSizeChanged(pageSize: PageSize) {
        _uiState.update { it.copy(pageSize = pageSize) }
    }

    fun onOrientationChanged(orientation: PageOrientation) {
        _uiState.update { it.copy(orientation = orientation) }
    }

    fun generatePdf() {
        val state = _uiState.value
        if (state.images.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please add at least one image") }
            return
        }

        _uiState.update { it.copy(isGenerating = true, errorMessage = null) }

        viewModelScope.launch {
            val params = ImageToPdfParams(
                outputFileName = state.fileName.ifBlank { "Document_${System.currentTimeMillis()}" },
                images = state.images,
                pageSize = state.pageSize,
                orientation = state.orientation,
                margin = state.margin
            )

            createPdfFromImagesUseCase(params)
                .onSuccess { doc ->
                    _uiState.update { it.copy(isGenerating = false, generatedPdf = doc) }
                }
                .onError { error ->
                    _uiState.update { it.copy(isGenerating = false, errorMessage = error.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
