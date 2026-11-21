package com.examplealam292.pdfmaker.core.common.extensions

import android.content.Context
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Long.formatAsFileSize(): String {
    if (this <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (Math.log10(this.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.size - 1)
    return String.format(Locale.getDefault(), "%.1f %s", this / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

fun Long.formatAsDate(pattern: String = "dd MMM yyyy, hh:mm a"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(Date(this))
}

fun File.isPdf(): Boolean = this.isFile && this.extension.equals("pdf", ignoreCase = true)
