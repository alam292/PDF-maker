package com.examplealam292.pdfmaker.data.local.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

private val Context.dataStore by preferencesDataStore(name = "pdf_documents_prefs")

class LocalPdfDataSource(private val context: Context) {

    private val pdfsKey = stringPreferencesKey("saved_pdf_list")

    val pdfListFlow: Flow<List<PdfDocument>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[pdfsKey] ?: "[]"
        parseJsonToPdfs(jsonString)
    }

    suspend fun savePdf(pdf: PdfDocument) {
        context.dataStore.edit { preferences ->
            val jsonString = preferences[pdfsKey] ?: "[]"
            val currentList = parseJsonToPdfs(jsonString).toMutableList()
            // Remove existing if any, then insert at top
            currentList.removeAll { it.id == pdf.id }
            currentList.add(0, pdf)
            preferences[pdfsKey] = serializePdfsToJson(currentList)
        }
    }

    suspend fun deletePdf(id: String) {
        context.dataStore.edit { preferences ->
            val jsonString = preferences[pdfsKey] ?: "[]"
            val currentList = parseJsonToPdfs(jsonString).filterNot { it.id == id }
            preferences[pdfsKey] = serializePdfsToJson(currentList)
        }
    }

    suspend fun updatePdf(pdf: PdfDocument) {
        context.dataStore.edit { preferences ->
            val jsonString = preferences[pdfsKey] ?: "[]"
            val currentList = parseJsonToPdfs(jsonString).map {
                if (it.id == pdf.id) pdf else it
            }
            preferences[pdfsKey] = serializePdfsToJson(currentList)
        }
    }

    private fun parseJsonToPdfs(jsonString: String): List<PdfDocument> {
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<PdfDocument>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val file = File(obj.getString("path"))
                if (file.exists()) {
                    list.add(
                        PdfDocument(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            file = file,
                            path = file.absolutePath,
                            sizeBytes = file.length(),
                            pageCount = obj.optInt("pageCount", 1),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                            modifiedAt = file.lastModified()
                        )
                    )
                }
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun serializePdfsToJson(list: List<PdfDocument>): String {
        val jsonArray = JSONArray()
        list.forEach { doc ->
            val obj = JSONObject().apply {
                put("id", doc.id)
                put("name", doc.name)
                put("path", doc.path)
                put("pageCount", doc.pageCount)
                put("createdAt", doc.createdAt)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }
}
