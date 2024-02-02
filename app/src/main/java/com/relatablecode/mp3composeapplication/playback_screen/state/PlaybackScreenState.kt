package com.relatablecode.mp3composeapplication.playback_screen.state


data class PlaybackScreenState(
    var playbackScreenEnum: PlaybackScreenEnum,
    var isMenuVisible: Boolean = true,
)
