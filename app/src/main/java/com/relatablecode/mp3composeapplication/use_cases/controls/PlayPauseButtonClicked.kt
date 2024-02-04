package com.relatablecode.mp3composeapplication.use_cases.controls

import android.net.Uri
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import com.relatablecode.mp3composeapplication.use_cases.music.PauseMusicUseCase
import com.relatablecode.mp3composeapplication.use_cases.music.PlayMusicUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow

class PlayPauseButtonClicked(
    private val playMusicUseCase: PlayMusicUseCase,
    private val pauseMusicUseCase: PauseMusicUseCase
) {

    suspend operator fun invoke(
        state: MutableStateFlow<PlaybackScreenState>,
        mp3PlayerEventChannel: Channel<MP3PlayerEvent>,
        uri: Uri
    ) {
        if (state.value.isPlayingSong) {
            playMusicUseCase.invoke(
                state = state,
                mp3PlayerEventChannel = mp3PlayerEventChannel,
                uri
            )
        } else {
            pauseMusicUseCase.invoke(
                state = state,
                mp3PlayerEventChannel = mp3PlayerEventChannel
            )
        }
    }

}
