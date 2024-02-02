package com.relatablecode.mp3composeapplication.playback_screen.state

import com.relatablecode.mp3composeapplication.Mp3Item


data class PlaybackScreenState(
    var playbackScreenEnum: PlaybackScreenEnum,
    var isMenuVisible: Boolean = true,
    var mp3Items: List<Mp3Item> = listOf(),
    var isPlayingSong: Boolean = false,
    var songBeingPlayed: Mp3Item? = null
)
