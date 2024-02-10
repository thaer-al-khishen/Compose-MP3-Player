package com.relatablecode.mp3composeapplication.use_cases.controls

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import javax.inject.Inject

class MiddleButtonLongClickedUseCase @Inject constructor() {

    operator fun invoke(
        currentState: PlaybackScreenState
    ): MiddleButtonLongClickedAction {
        return when {
            currentState.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST && !currentState.isMenuVisible -> MiddleButtonLongClickedAction.DELETE_SONG
            else -> MiddleButtonLongClickedAction.DO_NOTHING
        }
    }

}

enum class MiddleButtonLongClickedAction {
    DELETE_SONG, DO_NOTHING
}