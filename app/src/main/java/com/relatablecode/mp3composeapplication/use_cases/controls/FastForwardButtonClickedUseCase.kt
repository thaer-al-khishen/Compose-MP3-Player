package com.relatablecode.mp3composeapplication.use_cases.controls

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FastForwardButtonClickedUseCase @Inject constructor() {

    // Adjust to accept the current state and return the new state
    operator fun invoke(currentState: PlaybackScreenState): PlaybackScreenState {
        val isMenuVisible = currentState.isMenuVisible
        val isInsideMusicListWithoutMenu =
            !currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST
        val isInsideSongsWithoutMenu =
            !currentState.isMenuVisible && currentState.playbackScreenEnum == PlaybackScreenEnum.SONG

        return when {
            isMenuVisible -> currentState.copy(
                playbackScreenEnum = getNextPlaybackScreen(
                    currentState.playbackScreenEnum
                )
            )

            isInsideMusicListWithoutMenu -> goDownInMusicList(currentState)
            isInsideSongsWithoutMenu -> {
                goDownInMusicList(currentState)
            }
            else -> currentState.copy(isMenuVisible = true)
        }
    }

    private fun goDownInMusicList(currentState: PlaybackScreenState): PlaybackScreenState {
        val currentIndex = currentState.mp3Items.indexOfFirst { it.isSelected }
        val nextIndex = (currentIndex + 1) % currentState.mp3Items.size
        val updatedItems = currentState.mp3Items.mapIndexed { index, item ->
            item.copy(isSelected = index == nextIndex)
        }
        return currentState.copy(mp3Items = updatedItems)
    }

    private fun getNextPlaybackScreen(currentScreen: PlaybackScreenEnum): PlaybackScreenEnum {
        val nextOrdinal = (currentScreen.ordinal + 1) % PlaybackScreenEnum.values().size
        return PlaybackScreenEnum.values()[nextOrdinal]
    }

}
