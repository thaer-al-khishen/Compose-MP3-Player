package com.relatablecode.mp3composeapplication.mp3_player_device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun MP3PlayerDeviceBackground(): Brush {
    val colors = listOf(
        Color(0xFF70c7df), // blue_summer_82
//        Color(0xFF59c0db), // blue_sky_dive
        Color(0xFF3eb9d7), // blue_thalassophile
//        Color(0xFF59c0db), // blue_sky_dive
        Color(0xFF70c7df)  // blue_summer_82
    )

    return Brush.horizontalGradient(colors = colors)

//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Brush.horizontalGradient(colors = colors))
//    ) {
//        // Your content here
//    }
}