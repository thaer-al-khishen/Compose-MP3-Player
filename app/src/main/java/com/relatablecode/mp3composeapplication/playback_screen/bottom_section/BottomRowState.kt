package com.relatablecode.mp3composeapplication.playback_screen.bottom_section

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum

data class BottomRowState(
    val isMenuVisible: Boolean,
    val playbackScreenEnum: PlaybackScreenEnum
)
