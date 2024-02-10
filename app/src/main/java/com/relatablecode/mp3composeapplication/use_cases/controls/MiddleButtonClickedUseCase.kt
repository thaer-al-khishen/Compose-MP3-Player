package com.relatablecode.mp3composeapplication.use_cases.controls

import android.net.Uri
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MiddleButtonClickedUseCase @Inject constructor() {
    operator fun invoke(currentState: PlaybackScreenState): MiddleButtonAction {

        val isInsideHome = currentState.playbackScreenEnum == PlaybackScreenEnum.HOME
        val isInsideMusicWithMenu =
            currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST
        val isInsideMusicWithoutMenuWithSongs =
            !currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST && currentState.mp3Items.isNotEmpty()

        val isInsideMusicWithoutMenuWithoutSongs = !currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST && currentState.mp3Items.isEmpty()

        return when {
            isInsideHome -> MiddleButtonAction.AccessMedia
            isInsideMusicWithMenu -> {
                if (currentState.mp3Items.isEmpty()) MiddleButtonAction.AccessMedia
                else MiddleButtonAction.HideMenuSelectFirstSong
            }
            isInsideMusicWithoutMenuWithSongs -> MiddleButtonAction.PlaySelectedSong
            isInsideMusicWithoutMenuWithoutSongs -> MiddleButtonAction.AccessMedia
            else -> MiddleButtonAction.ShowMenu
        }
    }

}

enum class MiddleButtonAction {
    AccessMedia,
    HideMenuSelectFirstSong,
    PlaySelectedSong,
    ShowMenu
}
