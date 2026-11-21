package com.examplealam292.pdfmaker.data.local.entity

/**
 * Data entity representing a saved PDF document in the app.
 */
data class PdfDocumentEntity(
    val id: String,
    val name: String,
    val filePath: String,
    val sizeBytes: Long,
    val pageCount: Int,
    val createdAt: Long,
    val modifiedAt: Long
)
