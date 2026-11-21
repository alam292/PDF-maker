package com.examplealam292.pdfmaker.data.repository

import com.examplealam292.pdfmaker.core.common.dispatchers.CoroutineDispatchers
import com.examplealam292.pdfmaker.core.common.result.AppError
import com.examplealam292.pdfmaker.core.common.result.AppResult
import com.examplealam292.pdfmaker.data.engine.PdfEngine
import com.examplealam292.pdfmaker.data.local.datasource.LocalPdfDataSource
import com.examplealam292.pdfmaker.domain.model.ImageToPdfParams
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import com.examplealam292.pdfmaker.domain.model.TextToPdfParams
import com.examplealam292.pdfmaker.domain.repository.PdfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

class PdfRepositoryImpl(
    private val localPdfDataSource: LocalPdfDataSource,
    private val pdfEngine: PdfEngine,
    private val dispatchers: CoroutineDispatchers
) : PdfRepository {

    override fun getAllPdfs(): Flow<List<PdfDocument>> = localPdfDataSource.pdfListFlow

    override suspend fun getPdfById(id: String): AppResult<PdfDocument> = withContext(dispatchers.io) {
        val list = localPdfDataSource.pdfListFlow.first()
        val doc = list.find { it.id == id }
        if (doc != null) {
            AppResult.Success(doc)
        } else {
            AppResult.Error(AppError.FileError("Document not found"))
        }
    }

    override suspend fun createPdfFromImages(params: ImageToPdfParams): AppResult<PdfDocument> = withContext(dispatchers.io) {
        val result = pdfEngine.createPdfFromImages(params)
        if (result is AppResult.Success) {
            localPdfDataSource.savePdf(result.data)
        }
        result
    }

    override suspend fun createPdfFromText(params: TextToPdfParams): AppResult<PdfDocument> = withContext(dispatchers.io) {
        val result = pdfEngine.createPdfFromText(params)
        if (result is AppResult.Success) {
            localPdfDataSource.savePdf(result.data)
        }
        result
    }

    override suspend fun deletePdf(id: String): AppResult<Unit> = withContext(dispatchers.io) {
        val list = localPdfDataSource.pdfListFlow.first()
        val doc = list.find { it.id == id }
        if (doc != null) {
            if (doc.file.exists()) {
                doc.file.delete()
            }
            localPdfDataSource.deletePdf(id)
            AppResult.Success(Unit)
        } else {
            AppResult.Error(AppError.FileError("File does not exist"))
        }
    }

    override suspend fun renamePdf(id: String, newName: String): AppResult<PdfDocument> = withContext(dispatchers.io) {
        val list = localPdfDataSource.pdfListFlow.first()
        val doc = list.find { it.id == id }
        if (doc != null) {
            val sanitized = if (newName.endsWith(".pdf", ignoreCase = true)) newName else "$newName.pdf"
            val targetFile = File(doc.file.parentFile, sanitized)
            val success = doc.file.renameTo(targetFile)
            if (success) {
                val updated = doc.copy(
                    name = sanitized,
                    file = targetFile,
                    path = targetFile.absolutePath,
                    modifiedAt = targetFile.lastModified()
                )
                localPdfDataSource.updatePdf(updated)
                AppResult.Success(updated)
            } else {
                AppResult.Error(AppError.FileError("Failed to rename file"))
            }
        } else {
            AppResult.Error(AppError.FileError("Document not found"))
        }
    }
}
