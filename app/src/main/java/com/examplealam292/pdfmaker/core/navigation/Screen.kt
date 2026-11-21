package com.examplealam292.pdfmaker.core.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ImageToPdf : Screen("image_to_pdf")
    data object TextToPdf : Screen("text_to_pdf")
    data object PdfViewer : Screen("pdf_viewer/{pdfId}") {
        fun createRoute(pdfId: String): String = "pdf_viewer/$pdfId"
    }
}
