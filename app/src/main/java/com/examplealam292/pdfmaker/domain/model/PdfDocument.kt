package com.examplealam292.pdfmaker.domain.model

import java.io.File

/**
 * Domain model representing a PDF document in the app.
 */
data class PdfDocument(
    val id: String,
    val name: String,
    val file: File,
    val path: String,
    val sizeBytes: Long,
    val pageCount: Int,
    val createdAt: Long,
    val modifiedAt: Long
)
