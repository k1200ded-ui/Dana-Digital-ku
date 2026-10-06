package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.toArgb
import com.example.data.AppDatabase
import com.example.ui.TransactionScreen
import com.example.ui.TransactionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(this)
        val viewModel = TransactionViewModel(db)

        setContent {
            MaterialTheme {
                val backgroundColor = MaterialTheme.colorScheme.background
                window.statusBarColor = backgroundColor.toArgb()
                window.navigationBarColor = backgroundColor.toArgb()

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    TransactionScreen(viewModel)
                }
            }
        }
    }
}
