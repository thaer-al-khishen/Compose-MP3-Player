package com.relatablecode.mp3composeapplication.black_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BlackScreen(modifier: Modifier = Modifier) {
    Box(
        // Apply external modifiers first, allowing them to override the following defaults
        modifier = modifier
            .then(
                Modifier
                    .size(300.dp) // Default size, can be overridden by external modifier
                    .background(Color.Black, RoundedCornerShape(12.dp))
            )
    ) {
        BlackScreenContent()
    }
}
