package com.relatablecode.mp3composeapplication.use_cases.controls

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewindButtonClickedUseCase @Inject constructor() {

    // Adjust the function to accept the current state and return the new state
    operator fun invoke(currentState: PlaybackScreenState): PlaybackScreenState {

        val isMenuVisible = currentState.isMenuVisible
        val isInsideMusicListWithoutMenu = !currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST
        val isInsideSongsWithoutMenu = !currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.SONG

        return when {
            isMenuVisible -> currentState.copy(playbackScreenEnum = getPreviousPlaybackScreen(currentState.playbackScreenEnum))
            isInsideMusicListWithoutMenu -> goUpInMusicList(currentState)
            isInsideSongsWithoutMenu -> currentState.copy(isMenuVisible = true) // Show menu for other cases
            else -> currentState.copy(isMenuVisible = true)
        }
    }

    // This function now returns the new state instead of modifying it directly
    private fun goUpInMusicList(currentState: PlaybackScreenState): PlaybackScreenState {
        val currentIndex = currentState.mp3Items.indexOfFirst { it.isSelected }
        if (currentIndex != -1) {
            val previousIndex = if (currentIndex - 1 < 0) currentState.mp3Items.size - 1 else currentIndex - 1
            val updatedItems = currentState.mp3Items.mapIndexed { index, item ->
                item.copy(isSelected = index == previousIndex)
            }
            return currentState.copy(mp3Items = updatedItems)
        } else {
            val previousIndex = 0
            val updatedItems = currentState.mp3Items.mapIndexed { index, item ->
                item.copy(isSelected = index == previousIndex)
            }
            return currentState.copy(mp3Items = updatedItems)
        }
    }

    private fun getPreviousPlaybackScreen(currentScreen: PlaybackScreenEnum): PlaybackScreenEnum {
        val totalEnumValues = PlaybackScreenEnum.values().size
        val previousOrdinal = (currentScreen.ordinal - 1 + totalEnumValues) % totalEnumValues
        return PlaybackScreenEnum.values()[previousOrdinal]
    }

}
