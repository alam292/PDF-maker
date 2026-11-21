package com.examplealam292.pdfmaker.domain.model

enum class PageSize(val widthPt: Int, val heightPt: Int, val displayName: String) {
    A4(595, 842, "A4 (595 x 842 pt)"),
    LETTER(612, 792, "Letter (612 x 792 pt)"),
    LEGAL(612, 1008, "Legal (612 x 1008 pt)"),
    A3(842, 1191, "A3 (842 x 1191 pt)"),
    A5(420, 595, "A5 (420 x 595 pt)")
}

enum class PageOrientation {
    PORTRAIT,
    LANDSCAPE,
    AUTO
}

data class PageMargin(
    val top: Int = 36,
    val bottom: Int = 36,
    val left: Int = 36,
    val right: Int = 36
) {
    companion object {
        val NONE = PageMargin(0, 0, 0, 0)
        val NORMAL = PageMargin(36, 36, 36, 36)
        val NARROW = PageMargin(18, 18, 18, 18)
    }
}
