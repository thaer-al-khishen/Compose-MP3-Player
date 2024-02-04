package com.relatablecode.mp3composeapplication.use_cases.general

import com.relatablecode.mp3composeapplication.Mp3Item
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class UpdateMp3ItemsUseCase(
    private val mp3Items: List<Mp3Item>,
) {

    operator fun invoke(state: MutableStateFlow<PlaybackScreenState>) {
        state.update {
            it.copy(mp3Items = mp3Items)
        }
    }

}
