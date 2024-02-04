package com.relatablecode.mp3composeapplication.use_cases.controls

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class RewindUseCase {

    operator fun invoke(state: MutableStateFlow<PlaybackScreenState>) {

        val isMenuVisible = state.value.isMenuVisible
        val isInsideMusicListWithoutMenu = !state.value.isMenuVisible && state.value.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST
        val isInsideSongsWithoutMenu = !state.value.isMenuVisible && state.value.playbackScreenEnum == PlaybackScreenEnum.SONG

        when {
            isMenuVisible -> {
                goLeftInMenu(state)
            }
            isInsideMusicListWithoutMenu -> {
                goUpInMusicList(state)
            }

            isInsideSongsWithoutMenu -> {
                //Go to previous song
                showMenu(state)
            }
            else -> {
                showMenu(state)
            }
        }
    }

    private fun goLeftInMenu(state: MutableStateFlow<PlaybackScreenState>) {
        state.update {
            it.copy(
                playbackScreenEnum = getPreviousPlaybackScreen(
                    it.playbackScreenEnum
                )
            )
        }
    }

    private fun goUpInMusicList(state: MutableStateFlow<PlaybackScreenState>) {
        val currentIndex = state.value.mp3Items.indexOfFirst { it.isSelected }
        if (currentIndex != -1) {
            val previousIndex = if (currentIndex - 1 < 0) state.value.mp3Items.size - 1 else currentIndex - 1
            state.update { currentState ->
                val updatedItems = currentState.mp3Items.mapIndexed { index, item ->
                    item.copy(isSelected = index == previousIndex)
                }
                currentState.copy(mp3Items = updatedItems)
            }
        }
    }

    private fun getPreviousPlaybackScreen(currentScreen: PlaybackScreenEnum): PlaybackScreenEnum {
        val totalEnumValues = PlaybackScreenEnum.values().size
        val previousOrdinal = (currentScreen.ordinal - 1 + totalEnumValues) % totalEnumValues
        return PlaybackScreenEnum.values()[previousOrdinal]
    }

    private fun showMenu(state: MutableStateFlow<PlaybackScreenState>) {
        state.update { it.copy(isMenuVisible = true) }
    }

}
