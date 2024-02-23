package com.relatablecode.mp3composeapplication.playback_screen.middle_section

import com.relatablecode.mp3composeapplication.Mp3Item
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum

data class MiddleRowState(
    var playbackScreenEnum: PlaybackScreenEnum,
    var mp3Items: List<Mp3Item> = listOf(),
    var songBeingPlayed: Mp3Item? = null
)
