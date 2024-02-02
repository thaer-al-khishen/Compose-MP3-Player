package com.relatablecode.mp3composeapplication.playback_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.relatablecode.mp3composeapplication.Theme
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState

@Composable
fun PlaybackScreen(modifier: Modifier = Modifier, playbackScreenState: PlaybackScreenState) {
    // Outer Box for border
    Box(
        modifier = modifier
            .then(
                Modifier
                    .border(BorderStroke(1.dp, Theme.PlaybackScreenBorderColor), RoundedCornerShape(12.dp)) // Set border color and width here
            )
    ) {
        // Inner Box for background and content
        Box(
            modifier = Modifier
                .matchParentSize() // Match the size of the outer Box
                .background(Theme.PlaybackScreenColor, RoundedCornerShape(12.dp))
        ) {
            BlackScreenContent(playbackScreenState = playbackScreenState)
        }
    }
}
