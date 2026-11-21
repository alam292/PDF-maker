package com.examplealam292.pdfmaker.domain.repository

import com.examplealam292.pdfmaker.core.common.result.AppResult
import com.examplealam292.pdfmaker.domain.model.ImageToPdfParams
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import com.examplealam292.pdfmaker.domain.model.TextToPdfParams
import kotlinx.coroutines.flow.Flow

interface PdfRepository {
    fun getAllPdfs(): Flow<List<PdfDocument>>
    suspend fun getPdfById(id: String): AppResult<PdfDocument>
    suspend fun createPdfFromImages(params: ImageToPdfParams): AppResult<PdfDocument>
    suspend fun createPdfFromText(params: TextToPdfParams): AppResult<PdfDocument>
    suspend fun deletePdf(id: String): AppResult<Unit>
    suspend fun renamePdf(id: String, newName: String): AppResult<PdfDocument>
}
