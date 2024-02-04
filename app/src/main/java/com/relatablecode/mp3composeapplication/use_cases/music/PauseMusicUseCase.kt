package com.relatablecode.mp3composeapplication.use_cases.music

import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PauseMusicUseCase {
    suspend operator fun invoke(
        state: MutableStateFlow<PlaybackScreenState>,
        mp3PlayerEventChannel: Channel<MP3PlayerEvent>
    ) {
        mp3PlayerEventChannel.send(
            MP3PlayerEvent.PauseSong
        )
        state.update {
            it.copy(
                playbackScreenEnum = PlaybackScreenEnum.SONG,
                isPlayingSong = false,
                songBeingPlayed = null
            )
        }
    }
}
