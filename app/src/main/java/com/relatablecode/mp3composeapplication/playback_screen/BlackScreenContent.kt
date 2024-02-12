package com.relatablecode.mp3composeapplication.playback_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import com.relatablecode.mp3composeapplication.playback_screen.bottom_section.PlaybackScreenBottomRow
import com.relatablecode.mp3composeapplication.playback_screen.middle_section.PlaybackScreenMiddleRow
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import com.relatablecode.mp3composeapplication.playback_screen.top_section.PlaybackScreenTopRow

@Composable
fun BlackScreenContent(modifier: Modifier = Modifier, playbackScreenState: PlaybackScreenState, exoPlayer: ExoPlayer) {
    Column(
        modifier = modifier.then(
            Modifier.fillMaxSize()
        ),
        verticalArrangement = Arrangement.SpaceBetween,  //Divides the content equally, like SpaceEvenly, but in this case, the start and end composables inside the content are at the edges of the screen
    ) {
        PlaybackScreenTopRow()
        PlaybackScreenMiddleRow(modifier = Modifier.weight(1f), playbackScreenState = playbackScreenState, exoPlayer = exoPlayer)
        PlaybackScreenBottomRow(playbackScreenState = playbackScreenState)
    }
}
