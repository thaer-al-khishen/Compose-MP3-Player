package com.relatablecode.mp3composeapplication.use_cases.controls

import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FastForwardUseCase {

    operator fun invoke(state: MutableStateFlow<PlaybackScreenState>) {

        val isMenuVisible = state.value.isMenuVisible
        val isInsideMusicListWithoutMenu = !state.value.isMenuVisible && state.value.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST
        val isInsideSongsWithoutMenu = !state.value.isMenuVisible && state.value.playbackScreenEnum == PlaybackScreenEnum.SONG

        when {
            isMenuVisible -> {
                goRightInMenu(state)
            }

            isInsideMusicListWithoutMenu -> {
                goDownInMusicList(state)
            }

            isInsideSongsWithoutMenu -> {
                //Go to next song
                showMenu(state)
            }

            else -> {
                showMenu(state)
            }
        }
    }

    private fun goRightInMenu(state: MutableStateFlow<PlaybackScreenState>) {
        state.update { it.copy(playbackScreenEnum = getNextPlaybackScreen(it.playbackScreenEnum)) }
    }

    private fun goDownInMusicList(state: MutableStateFlow<PlaybackScreenState>) {
        val currentIndex = state.value.mp3Items.indexOfFirst { it.isSelected }
        val nextIndex = (currentIndex + 1) % state.value.mp3Items.size
        state.update { currentState ->
            val updatedItems = currentState.mp3Items.mapIndexed { index, item ->
                item.copy(isSelected = index == nextIndex)
            }
            currentState.copy(mp3Items = updatedItems)
        }
    }

    private fun getNextPlaybackScreen(currentScreen: PlaybackScreenEnum): PlaybackScreenEnum {
        val nextOrdinal = (currentScreen.ordinal + 1) % PlaybackScreenEnum.values().size
        return PlaybackScreenEnum.values()[nextOrdinal]
    }

    private fun showMenu(state: MutableStateFlow<PlaybackScreenState>) {
        state.update { it.copy(isMenuVisible = true) }
    }

}
