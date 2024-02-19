package com.relatablecode.mp3composeapplication.mp3_player_device

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.relatablecode.mp3composeapplication.Theme
import com.relatablecode.mp3composeapplication.theme.LocalAppTheme

@Composable
fun mp3PlayerDeviceBackground(): Brush {
    val colors = listOf(
        LocalAppTheme.current.primaryColor,
        LocalAppTheme.current.secondaryColor,
        LocalAppTheme.current.primaryColor
    )
    return Brush.horizontalGradient(colors = colors)
}
