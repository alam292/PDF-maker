package com.examplealam292.pdfmaker.domain.usecase

import com.examplealam292.pdfmaker.core.common.result.AppResult
import com.examplealam292.pdfmaker.domain.model.ImageToPdfParams
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import com.examplealam292.pdfmaker.domain.repository.PdfRepository

class CreatePdfFromImagesUseCase(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(params: ImageToPdfParams): AppResult<PdfDocument> {
        return pdfRepository.createPdfFromImages(params)
    }
}
