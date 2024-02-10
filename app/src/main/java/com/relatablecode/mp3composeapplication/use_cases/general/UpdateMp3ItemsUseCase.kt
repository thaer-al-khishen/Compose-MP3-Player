package com.relatablecode.mp3composeapplication.use_cases.general

import com.relatablecode.mp3composeapplication.Mp3Item
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateMp3ItemsUseCase @Inject constructor() {

    operator fun invoke(state: PlaybackScreenState, mp3Items: List<Mp3Item>): PlaybackScreenState {
        return state.copy(mp3Items = mp3Items)
    }

}
