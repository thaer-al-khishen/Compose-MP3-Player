package com.relatablecode.mp3composeapplication.playback_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.relatablecode.mp3composeapplication.playback_screen.bottom_section.BottomRowState
import com.relatablecode.mp3composeapplication.playback_screen.bottom_section.PlaybackScreenBottomRow
import com.relatablecode.mp3composeapplication.playback_screen.middle_section.MiddleRowState
import com.relatablecode.mp3composeapplication.playback_screen.middle_section.PlaybackScreenMiddleRow
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import com.relatablecode.mp3composeapplication.playback_screen.top_section.PlaybackScreenTopRow

@Composable
fun PlaybackScreenContent(modifier: Modifier = Modifier, playbackScreenState: PlaybackScreenState) {

    val middleRowState = remember(
        playbackScreenState.playbackScreenEnum,
        playbackScreenState.mp3Items,
        playbackScreenState.songBeingPlayed
    ) {
        MiddleRowState(
            playbackScreenEnum = playbackScreenState.playbackScreenEnum,
            mp3Items = playbackScreenState.mp3Items,
            songBeingPlayed = playbackScreenState.songBeingPlayed
        )
    }

    val bottomRowState =
        remember(playbackScreenState.isMenuVisible, playbackScreenState.playbackScreenEnum) {
            BottomRowState(
                isMenuVisible = playbackScreenState.isMenuVisible,
                playbackScreenEnum = playbackScreenState.playbackScreenEnum
            )
        }

    Column(
        modifier = modifier.then(
            Modifier.fillMaxSize()
        ),
        verticalArrangement = Arrangement.SpaceBetween,  //Divides the content equally, like SpaceEvenly, but in this case, the start and end composables inside the content are at the edges of the screen
    ) {
        PlaybackScreenTopRow()  //Time and battery indicators
        PlaybackScreenMiddleRow(
            modifier = Modifier.weight(1f),
            middleRowState = middleRowState
        )
        PlaybackScreenBottomRow(
            bottomRowState = bottomRowState
        )  //Bottom Menu
    }

}
