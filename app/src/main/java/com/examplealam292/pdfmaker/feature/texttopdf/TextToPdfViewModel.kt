package com.examplealam292.pdfmaker.feature.texttopdf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examplealam292.pdfmaker.core.common.result.onError
import com.examplealam292.pdfmaker.core.common.result.onSuccess
import com.examplealam292.pdfmaker.domain.model.PageSize
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import com.examplealam292.pdfmaker.domain.model.TextToPdfParams
import com.examplealam292.pdfmaker.domain.usecase.CreatePdfFromTextUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TextToPdfUiState(
    val fileName: String = "Notes_${System.currentTimeMillis() / 1000}",
    val title: String = "",
    val content: String = "",
    val pageSize: PageSize = PageSize.A4,
    val isGenerating: Boolean = false,
    val generatedPdf: PdfDocument? = null,
    val errorMessage: String? = null
)

class TextToPdfViewModel(
    private val createPdfFromTextUseCase: CreatePdfFromTextUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TextToPdfUiState())
    val uiState: StateFlow<TextToPdfUiState> = _uiState.asStateFlow()

    fun onFileNameChanged(name: String) = _uiState.update { it.copy(fileName = name) }
    fun onTitleChanged(title: String) = _uiState.update { it.copy(title = title) }
    fun onContentChanged(content: String) = _uiState.update { it.copy(content = content) }

    fun generatePdf() {
        val state = _uiState.value
        if (state.content.isBlank() && state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter some text") }
            return
        }

        _uiState.update { it.copy(isGenerating = true, errorMessage = null) }

        viewModelScope.launch {
            val params = TextToPdfParams(
                outputFileName = state.fileName.ifBlank { "TextDoc_${System.currentTimeMillis()}" },
                title = state.title,
                content = state.content,
                pageSize = state.pageSize
            )

            createPdfFromTextUseCase(params)
                .onSuccess { doc ->
                    _uiState.update { it.copy(isGenerating = false, generatedPdf = doc) }
                }
                .onError { error ->
                    _uiState.update { it.copy(isGenerating = false, errorMessage = error.message) }
                }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}
