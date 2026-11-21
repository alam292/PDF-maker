package com.examplealam292.pdfmaker.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream
import kotlin.math.max

object ImageUtils {

    /**
     * Efficiently loads and downsamples a bitmap from a Uri avoiding OutOfMemory (OOM).
     */
    fun decodeSampledBitmapFromUri(
        context: Context,
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int,
        rotationDegrees: Int = 0
    ): Bitmap? {
        val resolver = context.contentResolver

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        resolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        } ?: return null

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        var decodedBitmap = resolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        } ?: return null

        // Check EXIF orientation
        val exifRotation = getExifRotation(resolver.openInputStream(uri))
        val totalRotation = (rotationDegrees + exifRotation) % 360

        if (totalRotation != 0) {
            val matrix = Matrix().apply { postRotate(totalRotation.toFloat()) }
            val rotatedBitmap = Bitmap.createBitmap(
                decodedBitmap, 0, 0,
                decodedBitmap.width, decodedBitmap.height,
                matrix, true
            )
            if (rotatedBitmap != decodedBitmap) {
                decodedBitmap.recycle()
                decodedBitmap = rotatedBitmap
            }
        }

        return decodedBitmap
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return max(1, inSampleSize)
    }

    private fun getExifRotation(inputStream: InputStream?): Int {
        if (inputStream == null) return 0
        return try {
            val exif = ExifInterface(inputStream)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        } finally {
            try { inputStream.close() } catch (_: Exception) {}
        }
    }
}
