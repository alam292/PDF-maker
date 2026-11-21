package com.examplealam292.pdfmaker.domain.usecase

import com.examplealam292.pdfmaker.core.common.result.AppResult
import com.examplealam292.pdfmaker.domain.repository.PdfRepository

class DeletePdfUseCase(
    private val pdfRepository: PdfRepository
) {
    suspend operator fun invoke(id: String): AppResult<Unit> {
        return pdfRepository.deletePdf(id)
    }
}
