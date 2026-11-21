package com.examplealam292.pdfmaker.core.common.result

/**
 * Standardized application error definitions.
 */
sealed interface AppError {
    val message: String
    val throwable: Throwable?

    data class FileError(
        override val message: String,
        override val throwable: Throwable? = null
    ) : AppError

    data class PdfProcessingError(
        override val message: String,
        override val throwable: Throwable? = null
    ) : AppError

    data class PermissionDenied(
        override val message: String = "Required permission was denied",
        override val throwable: Throwable? = null
    ) : AppError

    data class OutOfMemory(
        override val message: String = "Out of memory while processing document",
        override val throwable: Throwable? = null
    ) : AppError

    data class Unknown(
        override val message: String = "An unexpected error occurred",
        override val throwable: Throwable? = null
    ) : AppError
}
