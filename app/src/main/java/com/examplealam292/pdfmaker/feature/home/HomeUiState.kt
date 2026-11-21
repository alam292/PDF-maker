package com.examplealam292.pdfmaker.feature.home

import com.examplealam292.pdfmaker.domain.model.PdfDocument

data class HomeUiState(
    val isLoading: Boolean = false,
    val pdfs: List<PdfDocument> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
) {
    val filteredPdfs: List<PdfDocument>
        get() = if (searchQuery.isBlank()) {
            pdfs
        } else {
            pdfs.filter { it.name.contains(searchQuery.trim(), ignoreCase = true) }
        }
}
