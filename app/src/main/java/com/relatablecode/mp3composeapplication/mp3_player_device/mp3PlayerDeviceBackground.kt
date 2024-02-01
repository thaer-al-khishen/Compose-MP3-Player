package com.relatablecode.mp3composeapplication.mp3_player_device

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.relatablecode.mp3composeapplication.Theme

@Composable
fun mp3PlayerDeviceBackground(): Brush {
    val colors = listOf(
        Theme.LightGray,
        Theme.DarkGray,
        Theme.LightGray
    )
    return Brush.horizontalGradient(colors = colors)
}
