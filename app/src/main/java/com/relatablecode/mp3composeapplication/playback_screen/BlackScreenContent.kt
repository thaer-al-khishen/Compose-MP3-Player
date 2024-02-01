package com.relatablecode.mp3composeapplication.playback_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.relatablecode.mp3composeapplication.playback_screen.bottom_section.PlaybackScreenBottomRow
import com.relatablecode.mp3composeapplication.playback_screen.middle_section.PlaybackScreenMiddleRow
import com.relatablecode.mp3composeapplication.playback_screen.top_section.PlaybackScreenTopRow

@Composable
fun BlackScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.then(
            Modifier.fillMaxSize()
        ),
        verticalArrangement = Arrangement.SpaceBetween,  //Divides the content equally, like SpaceEvenly, but in this case, the start and end composables inside the content are at the edges of the screen
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PlaybackScreenTopRow()
        PlaybackScreenMiddleRow()
        PlaybackScreenBottomRow()
    }
}
