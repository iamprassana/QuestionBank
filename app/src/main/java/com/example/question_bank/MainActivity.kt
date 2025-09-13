package com.example.question_bank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.question_bank.navigation.NavigationStack
import com.example.question_bank.ui.theme.Question_bankTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Question_bankTheme {
                NavigationStack()
            }
        }
    }
}
