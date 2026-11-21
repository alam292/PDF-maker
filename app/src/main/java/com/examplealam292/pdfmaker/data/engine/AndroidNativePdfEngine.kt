package com.examplealam292.pdfmaker.data.engine

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument as AndroidPdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.examplealam292.pdfmaker.core.common.result.AppError
import com.examplealam292.pdfmaker.core.common.result.AppResult
import com.examplealam292.pdfmaker.core.utils.ImageUtils
import com.examplealam292.pdfmaker.domain.model.ImageToPdfParams
import com.examplealam292.pdfmaker.domain.model.PageOrientation
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import com.examplealam292.pdfmaker.domain.model.TextToPdfParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

interface PdfEngine {
    suspend fun createPdfFromImages(params: ImageToPdfParams): AppResult<PdfDocument>
    suspend fun createPdfFromText(params: TextToPdfParams): AppResult<PdfDocument>
}

class AndroidNativePdfEngine(
    private val context: Context
) : PdfEngine {

    private val outputDir: File by lazy {
        File(context.filesDir, "pdfs").apply { if (!exists()) mkdirs() }
    }

    override suspend fun createPdfFromImages(params: ImageToPdfParams): AppResult<PdfDocument> = withContext(Dispatchers.IO) {
        val document = AndroidPdfDocument()
        val uniqueId = UUID.randomUUID().toString()
        val sanitizedName = if (params.outputFileName.endsWith(".pdf", ignoreCase = true)) {
            params.outputFileName
        } else {
            "${params.outputFileName}.pdf"
        }
        val outputFile = File(outputDir, "${System.currentTimeMillis()}_$sanitizedName")

        try {
            params.images.forEachIndexed { index, imageItem ->
                val (pageWidth, pageHeight) = calculatePageDimensions(params)
                val pageInfo = AndroidPdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
                val page = document.startPage(pageInfo)
                val canvas = page.canvas

                // Background white
                canvas.drawColor(Color.WHITE)

                val contentWidth = pageWidth - params.margin.left - params.margin.right
                val contentHeight = pageHeight - params.margin.top - params.margin.bottom

                val bitmap = ImageUtils.decodeSampledBitmapFromUri(
                    context = context,
                    uri = imageItem.uri,
                    reqWidth = contentWidth,
                    reqHeight = contentHeight,
                    rotationDegrees = imageItem.rotationDegrees
                )

                if (bitmap != null) {
                    val scale = minOf(
                        contentWidth.toFloat() / bitmap.width.toFloat(),
                        contentHeight.toFloat() / bitmap.height.toFloat()
                    )
                    val targetWidth = (bitmap.width * scale).toInt()
                    val targetHeight = (bitmap.height * scale).toInt()

                    val left = params.margin.left + (contentWidth - targetWidth) / 2
                    val top = params.margin.top + (contentHeight - targetHeight) / 2
                    val destRect = Rect(left, top, left + targetWidth, top + targetHeight)

                    val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
                    canvas.drawBitmap(bitmap, null, destRect, paint)
                    bitmap.recycle()
                }

                document.finishPage(page)
            }

            FileOutputStream(outputFile).use { outStream ->
                document.writeTo(outStream)
            }
            document.close()

            val pdfDocument = PdfDocument(
                id = uniqueId,
                name = sanitizedName,
                file = outputFile,
                path = outputFile.absolutePath,
                sizeBytes = outputFile.length(),
                pageCount = params.images.size,
                createdAt = System.currentTimeMillis(),
                modifiedAt = outputFile.lastModified()
            )
            AppResult.Success(pdfDocument)
        } catch (e: OutOfMemoryError) {
            document.close()
            if (outputFile.exists()) outputFile.delete()
            AppResult.Error(AppError.OutOfMemory(throwable = e))
        } catch (e: Exception) {
            document.close()
            if (outputFile.exists()) outputFile.delete()
            AppResult.Error(AppError.PdfProcessingError(message = e.localizedMessage ?: "Failed to generate PDF", throwable = e))
        }
    }

    override suspend fun createPdfFromText(params: TextToPdfParams): AppResult<PdfDocument> = withContext(Dispatchers.IO) {
        val document = AndroidPdfDocument()
        val uniqueId = UUID.randomUUID().toString()
        val sanitizedName = if (params.outputFileName.endsWith(".pdf", ignoreCase = true)) {
            params.outputFileName
        } else {
            "${params.outputFileName}.pdf"
        }
        val outputFile = File(outputDir, "${System.currentTimeMillis()}_$sanitizedName")

        try {
            val pageWidth = params.pageSize.widthPt
            val pageHeight = params.pageSize.heightPt
            val contentWidth = pageWidth - params.margin.left - params.margin.right

            val titlePaint = TextPaint().apply {
                isAntiAlias = true
                textSize = params.fontSize + 6f
                color = Color.BLACK
                isFakeBoldText = true
            }

            val bodyPaint = TextPaint().apply {
                isAntiAlias = true
                textSize = params.fontSize
                color = Color.DKGRAY
            }

            val pageInfo = AndroidPdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawColor(Color.WHITE)

            var currentY = params.margin.top.toFloat()

            // Draw title
            if (params.title.isNotBlank()) {
                val titleLayout = createStaticLayout(params.title, titlePaint, contentWidth)
                canvas.save()
                canvas.translate(params.margin.left.toFloat(), currentY)
                titleLayout.draw(canvas)
                canvas.restore()
                currentY += titleLayout.height + 16f
            }

            // Draw body text
            if (params.content.isNotBlank()) {
                val bodyLayout = createStaticLayout(params.content, bodyPaint, contentWidth)
                canvas.save()
                canvas.translate(params.margin.left.toFloat(), currentY)
                bodyLayout.draw(canvas)
                canvas.restore()
            }

            document.finishPage(page)

            FileOutputStream(outputFile).use { outStream ->
                document.writeTo(outStream)
            }
            document.close()

            val pdfDocument = PdfDocument(
                id = uniqueId,
                name = sanitizedName,
                file = outputFile,
                path = outputFile.absolutePath,
                sizeBytes = outputFile.length(),
                pageCount = 1,
                createdAt = System.currentTimeMillis(),
                modifiedAt = outputFile.lastModified()
            )
            AppResult.Success(pdfDocument)
        } catch (e: Exception) {
            document.close()
            if (outputFile.exists()) outputFile.delete()
            AppResult.Error(AppError.PdfProcessingError(message = e.localizedMessage ?: "Failed to generate PDF from text", throwable = e))
        }
    }

    private fun calculatePageDimensions(params: ImageToPdfParams): Pair<Int, Int> {
        val w = params.pageSize.widthPt
        val h = params.pageSize.heightPt
        return when (params.orientation) {
            PageOrientation.PORTRAIT -> Pair(minOf(w, h), maxOf(w, h))
            PageOrientation.LANDSCAPE -> Pair(maxOf(w, h), minOf(w, h))
            PageOrientation.AUTO -> Pair(w, h)
        }
    }

    @Suppress("DEPRECATION")
    private fun createStaticLayout(text: CharSequence, paint: TextPaint, width: Int): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.2f)
                .setIncludePad(true)
                .build()
        } else {
            StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.2f, 0f, true)
        }
    }
}
