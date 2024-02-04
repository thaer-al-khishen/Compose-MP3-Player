package com.relatablecode.mp3composeapplication.use_cases.music

import android.net.Uri
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PlayMusicUseCase {

    suspend operator fun invoke(
        state: MutableStateFlow<PlaybackScreenState>,
        mp3PlayerEventChannel: Channel<MP3PlayerEvent>,
        uri: Uri
    ) {
        mp3PlayerEventChannel.send(MP3PlayerEvent.PlaySong(uri))
        state.update { currentState ->
            currentState.copy(isMenuVisible = true,
                playbackScreenEnum = PlaybackScreenEnum.SONG,
                isPlayingSong = true,
                songBeingPlayed = currentState.mp3Items.find { it.uri == uri })
        }
    }

}
