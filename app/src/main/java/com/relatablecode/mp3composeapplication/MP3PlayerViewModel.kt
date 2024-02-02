package com.relatablecode.mp3composeapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.relatablecode.mp3composeapplication.circular_control_panel.CircularControlClickEvent
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MP3PlayerViewModel : ViewModel() {

    private val _playbackScreenState = MutableStateFlow(
        PlaybackScreenState(
            playbackScreenEnum = PlaybackScreenEnum.HOME,
            isMenuVisible = true,
        )
    )
    val playbackScreenState: StateFlow<PlaybackScreenState> = _playbackScreenState

    private val _playbackScreenEvent = Channel<CircularControlClickEvent>(Channel.BUFFERED)
    val playbackScreenEvent = _playbackScreenEvent.receiveAsFlow()

    private val _mp3PlayerEvent = Channel<MP3PlayerEvent>(Channel.BUFFERED)
    val mp3PlayerEvent = _mp3PlayerEvent.receiveAsFlow()

    fun onEvent(event: CircularControlClickEvent) {
        when (event) {
            CircularControlClickEvent.OnMenuClicked -> {
                _playbackScreenState.update { it.copy(isMenuVisible = !it.isMenuVisible) }
            }

            CircularControlClickEvent.OnRewindClicked -> {
                handleRewindClicked(
                    playbackScreenState.value.playbackScreenEnum,
                    playbackScreenState.value.isMenuVisible
                )
            }

            CircularControlClickEvent.OnFastForwardClicked -> {
                viewModelScope.launch {
                    _mp3PlayerEvent.send(MP3PlayerEvent.AccessMediaSingleFile)
                }
//                handleFastForwardClicked(
//                    playbackScreenState.value.playbackScreenEnum,
//                    playbackScreenState.value.isMenuVisible
//                )
            }

            CircularControlClickEvent.OnPlayPauseClicked -> {
                handlePlayPauseClicked()
            }

            CircularControlClickEvent.OnMiddleButtonClicked -> {
                handleMiddleButtonClicked(
                    playbackScreenState.value.playbackScreenEnum,
                    playbackScreenState.value.isMenuVisible
                )
            }

            CircularControlClickEvent.Default -> {}
        }
    }

    private fun handleRewindClicked(
        playbackScreenEnum: PlaybackScreenEnum,
        isMenuVisible: Boolean
    ) {
        when {
            isMenuVisible -> {
                _playbackScreenState.update {
                    it.copy(
                        playbackScreenEnum = getPreviousPlaybackScreen(
                            it.playbackScreenEnum
                        )
                    )
                }
            }

            !isMenuVisible && playbackScreenEnum == PlaybackScreenEnum.MP3_FILE -> {
                //Go to previous file
                _playbackScreenState.update { it.copy(isMenuVisible = true) }
            }

            else -> {
                _playbackScreenState.update { it.copy(isMenuVisible = true) }
            }
        }
    }

    private fun handleFastForwardClicked(
        playbackScreenEnum: PlaybackScreenEnum,
        isMenuVisible: Boolean
    ) {
        when {
            isMenuVisible -> {
                _playbackScreenState.update { it.copy(playbackScreenEnum = getNextPlaybackScreen(it.playbackScreenEnum)) }
            }

            !isMenuVisible && playbackScreenEnum == PlaybackScreenEnum.MP3_FILE -> {
                //Go to next file
                _playbackScreenState.update { it.copy(isMenuVisible = true) }
            }

            else -> {
                _playbackScreenState.update { it.copy(isMenuVisible = true) }
            }
        }
    }

    private fun handlePlayPauseClicked() {
        //Play music
    }

    private fun handleMiddleButtonClicked(
        playbackScreenEnum: PlaybackScreenEnum,
        isMenuVisible: Boolean
    ) {
        when {
            !isMenuVisible && playbackScreenEnum == PlaybackScreenEnum.MP3_FILE -> {
                //Maybe choose a certain file?
                //And:
                _playbackScreenState.update { it.copy(isMenuVisible = true) }
            }
            else -> {
                _playbackScreenState.update { it.copy(isMenuVisible = !isMenuVisible) }
            }
        }
    }

    private fun getNextPlaybackScreen(currentScreen: PlaybackScreenEnum): PlaybackScreenEnum {
        val nextOrdinal = (currentScreen.ordinal + 1) % PlaybackScreenEnum.values().size
        return PlaybackScreenEnum.values()[nextOrdinal]
    }

    private fun getPreviousPlaybackScreen(currentScreen: PlaybackScreenEnum): PlaybackScreenEnum {
        val totalEnumValues = PlaybackScreenEnum.values().size
        val previousOrdinal = (currentScreen.ordinal - 1 + totalEnumValues) % totalEnumValues
        return PlaybackScreenEnum.values()[previousOrdinal]
    }

}
