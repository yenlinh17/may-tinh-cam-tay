package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.calculator.screens.CalculatorScreen
import com.example.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                // Nền đen toàn bộ màn hình
                Surface(
                    modifier = Modifier
                        .fillMaxSize() // quan trọng: chiếm toàn bộ
                        .background(Color.Black)
                        .windowInsetsPadding(
                            WindowInsets.systemBars
                                .only(WindowInsetsSides.Horizontal) // padding hai bên (nếu cần)
                        ),
                    color = Color.Black // đảm bảo Surface cũng có nền đen
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

