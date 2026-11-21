package com.examplealam292.pdfmaker.feature.viewer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examplealam292.pdfmaker.core.common.result.onError
import com.examplealam292.pdfmaker.core.common.result.onSuccess
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import com.examplealam292.pdfmaker.domain.repository.PdfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PdfViewerUiState(
    val isLoading: Boolean = true,
    val document: PdfDocument? = null,
    val pages: List<Bitmap> = emptyList(),
    val errorMessage: String? = null
)

class PdfViewerViewModel(
    private val pdfRepository: PdfRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PdfViewerUiState())
    val uiState: StateFlow<PdfViewerUiState> = _uiState.asStateFlow()

    fun loadPdf(pdfId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            pdfRepository.getPdfById(pdfId)
                .onSuccess { doc ->
                    _uiState.update { it.copy(document = doc) }
                    renderPages(doc)
                }
                .onError { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    private fun renderPages(doc: PdfDocument) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileDescriptor = ParcelFileDescriptor.open(doc.file, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(fileDescriptor)
                val renderedPages = mutableListOf<Bitmap>()

                for (i in 0 until pdfRenderer.pageCount) {
                    val page = pdfRenderer.openPage(i)
                    // High resolution bitmap for crisp viewing
                    val bitmap = Bitmap.createBitmap(
                        page.width * 2,
                        page.height * 2,
                        Bitmap.Config.ARGB_8888
                    )
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    renderedPages.add(bitmap)
                    page.close()
                }

                pdfRenderer.close()
                fileDescriptor.close()

                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false, pages = renderedPages) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to render PDF pages: ${e.localizedMessage}"
                        )
                    }
                }
            }
        }
    }
}
