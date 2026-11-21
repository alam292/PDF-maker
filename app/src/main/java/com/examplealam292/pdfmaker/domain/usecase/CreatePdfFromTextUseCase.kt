package com.examplealam292.pdfmaker.domain.usecase

import com.examplealam292.pdfmaker.core.common.result.AppResult
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import com.examplealam292.pdfmaker.domain.model.TextToPdfParams
import com.examplealam292.pdfmaker.domain.repository.PdfRepository

class CreatePdfFromTextUseCase(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(params: TextToPdfParams): AppResult<PdfDocument> {
        return pdfRepository.createPdfFromText(params)
    }
}
