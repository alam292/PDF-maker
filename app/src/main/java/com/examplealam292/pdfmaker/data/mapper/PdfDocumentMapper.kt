package com.examplealam292.pdfmaker.data.mapper

import com.examplealam292.pdfmaker.data.local.entity.PdfDocumentEntity
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import java.io.File

fun PdfDocumentEntity.toDomain(): PdfDocument {
    val file = File(filePath)
    return PdfDocument(
        id = id,
        name = name,
        file = file,
        path = filePath,
        sizeBytes = if (file.exists()) file.length() else sizeBytes,
        pageCount = pageCount,
        createdAt = createdAt,
        modifiedAt = if (file.exists()) file.lastModified() else modifiedAt
    )
}

fun PdfDocument.toEntity(): PdfDocumentEntity {
    return PdfDocumentEntity(
        id = id,
        name = name,
        filePath = path,
        sizeBytes = sizeBytes,
        pageCount = pageCount,
        createdAt = createdAt,
        modifiedAt = modifiedAt
    )
}
