package com.examplealam292.pdfmaker.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examplealam292.pdfmaker.core.common.result.onError
import com.examplealam292.pdfmaker.domain.usecase.DeletePdfUseCase
import com.examplealam292.pdfmaker.domain.usecase.GetAllPdfsUseCase
import com.examplealam292.pdfmaker.domain.usecase.RenamePdfUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    getAllPdfsUseCase: GetAllPdfsUseCase,
    private val deletePdfUseCase: DeletePdfUseCase,
    private val renamePdfUseCase: RenamePdfUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        getAllPdfsUseCase()
            .onEach { pdfList ->
                _uiState.update { it.copy(pdfs = pdfList, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun deletePdf(id: String) {
        viewModelScope.launch {
            deletePdfUseCase(id).onError { error ->
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }

    fun renamePdf(id: String, newName: String) {
        viewModelScope.launch {
            renamePdfUseCase(id, newName).onError { error ->
                _uiState.update { it.copy(error = error.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
