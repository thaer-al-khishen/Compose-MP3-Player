package com.relatablecode.mp3composeapplication.use_cases.controls

import android.net.Uri
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayPauseButtonClickedUseCase @Inject constructor() {

    // Returns a Boolean indicating whether music should be played (true) or paused (false)
    operator fun invoke(currentState: PlaybackScreenState, uri: Uri?): Pair<Boolean, Uri?> {
        return Pair(!currentState.isPlayingSong, uri)
    }

}
