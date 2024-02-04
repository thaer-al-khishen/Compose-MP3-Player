package com.relatablecode.mp3composeapplication.use_cases.controls

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MenuClickedUseCase(
    private val state: MutableStateFlow<PlaybackScreenState>
) {
    operator fun invoke() {
        state.update { it.copy(isMenuVisible = !it.isMenuVisible) }
    }
}
