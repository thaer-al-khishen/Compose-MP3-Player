package com.relatablecode.mp3composeapplication.use_cases.controls

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MiddleButtonClickedUseCase @Inject constructor() {
    operator fun invoke(currentState: PlaybackScreenState): MiddleButtonAction {

        val isInsideHome = currentState.playbackScreenEnum == PlaybackScreenEnum.HOME
        val isInsideMusicWithMenu =
            currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST
        val isInsideSongsWithMenu =
            currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.SONG
        val isInsideMusicWithoutMenuWithSongs =
            !currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST && currentState.mp3Items.isNotEmpty()

        val isInsideMusicWithoutMenuWithoutSongs =
            !currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST && currentState.mp3Items.isEmpty()
        val isInsideSettingsWithMenu =
            currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.SETTINGS

        return when {
            isInsideHome -> MiddleButtonAction.AccessMedia
            isInsideMusicWithMenu -> {
                if (currentState.mp3Items.isEmpty()) MiddleButtonAction.AccessMedia
                else MiddleButtonAction.HideMenuSelectFirstSong
            }

            isInsideMusicWithoutMenuWithSongs -> MiddleButtonAction.PlaySelectedSong
            isInsideMusicWithoutMenuWithoutSongs -> MiddleButtonAction.AccessMedia
            isInsideSongsWithMenu -> MiddleButtonAction.HideMenu
            isInsideSettingsWithMenu -> MiddleButtonAction.HideMenu
            else -> MiddleButtonAction.ShowMenu
        }
    }

}

enum class MiddleButtonAction {
    AccessMedia, HideMenuSelectFirstSong, PlaySelectedSong, ShowMenu, HideMenu
}
