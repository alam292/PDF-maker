package com.examplealam292.pdfmaker.domain.model

import android.net.Uri

data class ImagePageItem(
    val uri: Uri,
    val rotationDegrees: Int = 0
)

data class ImageToPdfParams(
    val outputFileName: String,
    val images: List<ImagePageItem>,
    val pageSize: PageSize = PageSize.A4,
    val orientation: PageOrientation = PageOrientation.PORTRAIT,
    val margin: PageMargin = PageMargin.NORMAL,
    val compressQuality: Int = 85
)

data class TextToPdfParams(
    val outputFileName: String,
    val title: String,
    val content: String,
    val pageSize: PageSize = PageSize.A4,
    val fontSize: Float = 12f,
    val margin: PageMargin = PageMargin.NORMAL
)
