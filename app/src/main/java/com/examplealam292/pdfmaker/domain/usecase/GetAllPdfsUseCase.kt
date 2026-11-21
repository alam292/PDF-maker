package com.examplealam292.pdfmaker.domain.usecase

import com.examplealam292.pdfmaker.domain.model.PdfDocument
import com.examplealam292.pdfmaker.domain.repository.PdfRepository
import kotlinx.coroutines.flow.Flow

class GetAllPdfsUseCase(
    private val pdfRepository: PdfRepository
) {
    operator fun invoke(): Flow<List<PdfDocument>> = pdfRepository.getAllPdfs()
}
