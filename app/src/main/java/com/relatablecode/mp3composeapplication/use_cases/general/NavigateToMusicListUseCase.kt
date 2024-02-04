package com.relatablecode.mp3composeapplication.use_cases.general

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigateToMusicListUseCase @Inject constructor() {

    operator fun invoke(state: MutableStateFlow<PlaybackScreenState>) {
        state.update {
            it.copy(
                playbackScreenEnum = PlaybackScreenEnum.MUSIC_LIST
            )
        }
    }

}
