package com.example.authenticsneaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.authenticsneaker.ui.theme.AuthenticSneakerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthenticSneakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AuthNavigation()
                }
            }
        }
    }
}

@Composable
fun SimpleTestScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
    Text(
            text = "Hello World",
            color = Color.Black,
            fontSize = 24.sp
        )
    }
}