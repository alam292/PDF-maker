package com.examplealam292.pdfmaker.feature.imagetopdf

import android.net.Uri
import com.examplealam292.pdfmaker.domain.model.ImagePageItem
import com.examplealam292.pdfmaker.domain.model.PageMargin
import com.examplealam292.pdfmaker.domain.model.PageOrientation
import com.examplealam292.pdfmaker.domain.model.PageSize
import com.examplealam292.pdfmaker.domain.model.PdfDocument

data class ImageToPdfUiState(
    val images: List<ImagePageItem> = emptyList(),
    val fileName: String = "Document_${System.currentTimeMillis() / 1000}",
    val pageSize: PageSize = PageSize.A4,
    val orientation: PageOrientation = PageOrientation.PORTRAIT,
    val margin: PageMargin = PageMargin.NORMAL,
    val isGenerating: Boolean = false,
    val generatedPdf: PdfDocument? = null,
    val errorMessage: String? = null
)
