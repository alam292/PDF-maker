package com.examplealam292.pdfmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.examplealam292.pdfmaker.core.designsystem.theme.PdfMakerTheme
import com.examplealam292.pdfmaker.core.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PdfMakerTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}
