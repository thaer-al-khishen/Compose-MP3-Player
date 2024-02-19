package com.relatablecode.mp3composeapplication.playback_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.relatablecode.mp3composeapplication.Theme
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import com.relatablecode.mp3composeapplication.theme.LocalAppTheme

@Composable
fun PlaybackScreen(modifier: Modifier = Modifier, playbackScreenState: PlaybackScreenState, exoPlayer: ExoPlayer) {
    // Outer Box for border
    Box(
        modifier = modifier
            .then(
                Modifier
                    .border(BorderStroke(1.dp, LocalAppTheme.current.playbackScreenBorderColor), RoundedCornerShape(12.dp)) // Set border color and width here
            )
    ) {
        // Inner Box for background and content
        Box(
            modifier = Modifier
                .matchParentSize() // Match the size of the outer Box
                .background(LocalAppTheme.current.playbackScreenColor, RoundedCornerShape(12.dp))
        ) {
            BlackScreenContent(playbackScreenState = playbackScreenState, exoPlayer = exoPlayer)
        }
    }
}
