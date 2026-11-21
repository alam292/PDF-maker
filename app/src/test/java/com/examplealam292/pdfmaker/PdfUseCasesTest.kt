package com.examplealam292.pdfmaker

import com.examplealam292.pdfmaker.core.common.result.AppResult
import com.examplealam292.pdfmaker.domain.model.PageMargin
import com.examplealam292.pdfmaker.domain.model.PageOrientation
import com.examplealam292.pdfmaker.domain.model.PageSize
import com.examplealam292.pdfmaker.domain.model.PdfDocument
import com.examplealam292.pdfmaker.domain.model.TextToPdfParams
import com.examplealam292.pdfmaker.domain.repository.PdfRepository
import com.examplealam292.pdfmaker.domain.usecase.CreatePdfFromTextUseCase
import com.examplealam292.pdfmaker.domain.usecase.DeletePdfUseCase
import com.examplealam292.pdfmaker.domain.usecase.GetAllPdfsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class PdfUseCasesTest {

    private lateinit var fakeRepository: FakePdfRepository
    private lateinit var getAllPdfsUseCase: GetAllPdfsUseCase
    private lateinit var createPdfFromTextUseCase: CreatePdfFromTextUseCase
    private lateinit var deletePdfUseCase: DeletePdfUseCase

    @Before
    fun setUp() {
        fakeRepository = FakePdfRepository()
        getAllPdfsUseCase = GetAllPdfsUseCase(fakeRepository)
        createPdfFromTextUseCase = CreatePdfFromTextUseCase(fakeRepository)
        deletePdfUseCase = DeletePdfUseCase(fakeRepository)
    }

    @Test
    fun getAllPdfsUseCase_returnsDocuments() = runBlocking {
        val result = getAllPdfsUseCase().first()
        assertEquals(1, result.size)
        assertEquals("Sample.pdf", result.first().name)
    }

    @Test
    fun createPdfFromTextUseCase_successfullyCreatesPdf() = runBlocking {
        val params = TextToPdfParams(
            outputFileName = "TestDoc.pdf",
            title = "Test Title",
            content = "Test content"
        )
        val result = createPdfFromTextUseCase(params)
        assertTrue(result is AppResult.Success)
        val pdf = (result as AppResult.Success).data
        assertEquals("TestDoc.pdf", pdf.name)
    }

    @Test
    fun deletePdfUseCase_removesPdf() = runBlocking {
        val result = deletePdfUseCase("sample-1")
        assertTrue(result is AppResult.Success)
    }
}

class FakePdfRepository : PdfRepository {
    private val fakeList = mutableListOf(
        PdfDocument(
            id = "sample-1",
            name = "Sample.pdf",
            file = File("fake/path/Sample.pdf"),
            path = "fake/path/Sample.pdf",
            sizeBytes = 1024L,
            pageCount = 1,
            createdAt = 1000L,
            modifiedAt = 1000L
        )
    )

    override fun getAllPdfs(): Flow<List<PdfDocument>> = flowOf(fakeList)

    override suspend fun getPdfById(id: String): AppResult<PdfDocument> {
        val doc = fakeList.find { it.id == id }
        return if (doc != null) AppResult.Success(doc) else AppResult.Error(com.examplealam292.pdfmaker.core.common.result.AppError.FileError("Not found"))
    }

    override suspend fun createPdfFromImages(params: com.examplealam292.pdfmaker.domain.model.ImageToPdfParams): AppResult<PdfDocument> {
        val doc = PdfDocument(
            id = "new-img",
            name = params.outputFileName,
            file = File(params.outputFileName),
            path = params.outputFileName,
            sizeBytes = 2048L,
            pageCount = params.images.size,
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )
        fakeList.add(doc)
        return AppResult.Success(doc)
    }

    override suspend fun createPdfFromText(params: TextToPdfParams): AppResult<PdfDocument> {
        val doc = PdfDocument(
            id = "new-text",
            name = params.outputFileName,
            file = File(params.outputFileName),
            path = params.outputFileName,
            sizeBytes = 1024L,
            pageCount = 1,
            createdAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )
        fakeList.add(doc)
        return AppResult.Success(doc)
    }

    override suspend fun deletePdf(id: String): AppResult<Unit> {
        fakeList.removeAll { it.id == id }
        return AppResult.Success(Unit)
    }

    override suspend fun renamePdf(id: String, newName: String): AppResult<PdfDocument> {
        val doc = fakeList.find { it.id == id }!!.copy(name = newName)
        return AppResult.Success(doc)
    }
}
