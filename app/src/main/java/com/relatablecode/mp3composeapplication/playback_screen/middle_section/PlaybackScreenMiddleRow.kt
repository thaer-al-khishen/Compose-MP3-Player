package com.relatablecode.mp3composeapplication.playback_screen.middle_section

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState

@Composable
fun PlaybackScreenMiddleRow(
    modifier: Modifier = Modifier, playbackScreenState: PlaybackScreenState = PlaybackScreenState(
        playbackScreenEnum = PlaybackScreenEnum.HOME, isMenuVisible = true
    )
) {
    when {
        playbackScreenState.playbackScreenEnum == PlaybackScreenEnum.MP3_FILE -> {
            PlaybackScreenMusic()
        }
        playbackScreenState.playbackScreenEnum == PlaybackScreenEnum.SETTINGS -> {
            PlaybackScreenSettings()
        }
        else -> {
            PlaybackScreenHome()
        }
    }
}
