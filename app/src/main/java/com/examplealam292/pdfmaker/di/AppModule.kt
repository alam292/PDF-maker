package com.examplealam292.pdfmaker.di

import com.examplealam292.pdfmaker.core.common.dispatchers.CoroutineDispatchers
import com.examplealam292.pdfmaker.core.common.dispatchers.DefaultCoroutineDispatchers
import com.examplealam292.pdfmaker.data.engine.AndroidNativePdfEngine
import com.examplealam292.pdfmaker.data.engine.PdfEngine
import com.examplealam292.pdfmaker.data.local.datasource.LocalPdfDataSource
import com.examplealam292.pdfmaker.data.repository.PdfRepositoryImpl
import com.examplealam292.pdfmaker.domain.repository.PdfRepository
import com.examplealam292.pdfmaker.domain.usecase.CreatePdfFromImagesUseCase
import com.examplealam292.pdfmaker.domain.usecase.CreatePdfFromTextUseCase
import com.examplealam292.pdfmaker.domain.usecase.DeletePdfUseCase
import com.examplealam292.pdfmaker.domain.usecase.GetAllPdfsUseCase
import com.examplealam292.pdfmaker.domain.usecase.RenamePdfUseCase
import com.examplealam292.pdfmaker.feature.home.HomeViewModel
import com.examplealam292.pdfmaker.feature.imagetopdf.ImageToPdfViewModel
import com.examplealam292.pdfmaker.feature.texttopdf.TextToPdfViewModel
import com.examplealam292.pdfmaker.feature.viewer.PdfViewerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Dispatchers
    single<CoroutineDispatchers> { DefaultCoroutineDispatchers() }

    // Data Sources & Engine
    single { LocalPdfDataSource(get()) }
    single<PdfEngine> { AndroidNativePdfEngine(get()) }

    // Repository
    single<PdfRepository> { PdfRepositoryImpl(get(), get(), get()) }

    // UseCases
    factory { GetAllPdfsUseCase(get()) }
    factory { CreatePdfFromImagesUseCase(get()) }
    factory { CreatePdfFromTextUseCase(get()) }
    factory { DeletePdfUseCase(get()) }
    factory { RenamePdfUseCase(get()) }

    // ViewModels
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { ImageToPdfViewModel(get()) }
    viewModel { TextToPdfViewModel(get()) }
    viewModel { PdfViewerViewModel(get(), get()) }
}
