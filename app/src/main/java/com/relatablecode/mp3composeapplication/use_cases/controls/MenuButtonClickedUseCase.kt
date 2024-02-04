package com.relatablecode.mp3composeapplication.use_cases.controls

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuButtonClickedUseCase @Inject constructor() {

    operator fun invoke(state: MutableStateFlow<PlaybackScreenState>) {
        state.update { it.copy(isMenuVisible = !it.isMenuVisible) }
    }

}
