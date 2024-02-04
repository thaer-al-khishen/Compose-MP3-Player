package com.relatablecode.mp3composeapplication.use_cases.controls

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MenuClickedUseCase {

    operator fun invoke(state: MutableStateFlow<PlaybackScreenState>) {
        state.update { it.copy(isMenuVisible = !it.isMenuVisible) }
    }

}
