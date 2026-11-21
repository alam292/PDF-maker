package com.examplealam292.pdfmaker.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.examplealam292.pdfmaker.feature.home.HomeScreen
import com.examplealam292.pdfmaker.feature.imagetopdf.ImageToPdfScreen
import com.examplealam292.pdfmaker.feature.texttopdf.TextToPdfScreen
import com.examplealam292.pdfmaker.feature.viewer.PdfViewerScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToImageToPdf = {
                    navController.navigate(Screen.ImageToPdf.route)
                },
                onNavigateToTextToPdf = {
                    navController.navigate(Screen.TextToPdf.route)
                },
                onNavigateToPdfViewer = { pdfId ->
                    navController.navigate(Screen.PdfViewer.createRoute(pdfId))
                }
            )
        }

        composable(Screen.ImageToPdf.route) {
            ImageToPdfScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToViewer = { pdfId ->
                    navController.navigate(Screen.PdfViewer.createRoute(pdfId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.TextToPdf.route) {
            TextToPdfScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToViewer = { pdfId ->
                    navController.navigate(Screen.PdfViewer.createRoute(pdfId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.PdfViewer.route,
            arguments = listOf(
                navArgument("pdfId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val pdfId = backStackEntry.arguments?.getString("pdfId") ?: ""
            PdfViewerScreen(
                pdfId = pdfId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
