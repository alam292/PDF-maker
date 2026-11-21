package com.examplealam292.pdfmaker.domain.usecase

import com.examplealam292.pdfmaker.core.common.result.AppResult
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import com.examplealam292.pdfmaker.domain.repository.PdfRepository

class RenamePdfUseCase(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(id: String, newName: String): AppResult<PdfDocument> {
        return pdfRepository.renamePdf(id, newName)
    }
}
