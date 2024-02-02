package com.relatablecode.mp3composeapplication

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.relatablecode.mp3composeapplication.circular_control_panel.CircularControlClickEvent
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import com.relatablecode.mp3composeapplication.repository.UriRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MP3PlayerViewModel(application: Application) : AndroidViewModel(application) {

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

    private val _uris = MutableStateFlow<Set<Uri>>(emptySet())
    val uris: StateFlow<Set<Uri>> = _uris.asStateFlow()

    private val uriRepository = UriRepository(application)

    init {
        collectUris()
    }

    private fun collectUris() {
        viewModelScope.launch {
            uriRepository.urisFlow.collect { uriStrings ->
                _uris.value = uriStrings.map { Uri.parse(it) }.toSet()
            }
        }
    }

    fun saveUri(uri: Uri) {
        viewModelScope.launch {
            uriRepository.saveUri(uri)
        }
    }

    fun deleteUri(uri: Uri) {
        viewModelScope.launch {
            uriRepository.deleteUri(uri)
        }
    }

    fun updateMp3Items(mp3Items: List<Mp3Item>) {
        viewModelScope.launch {
            _playbackScreenState.update {
                it.copy(mp3Items = mp3Items)
            }
        }
    }

    fun navigateToMusicList() {
        _playbackScreenState.update {
            it.copy(
                playbackScreenEnum = PlaybackScreenEnum.MUSIC_LIST
            )
        }
    }

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
                handleFastForwardClicked(
                    playbackScreenState.value.playbackScreenEnum,
                    playbackScreenState.value.isMenuVisible
                )
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

            !isMenuVisible && playbackScreenEnum == PlaybackScreenEnum.SONG -> {
                //Go to previous song
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

            !isMenuVisible && playbackScreenEnum == PlaybackScreenEnum.SONG -> {
                //Go to next song
                _playbackScreenState.update { it.copy(isMenuVisible = true) }
            }

            else -> {
                _playbackScreenState.update { it.copy(isMenuVisible = true) }
            }
        }
    }

    private fun handlePlayPauseClicked() {
        //Play music
        viewModelScope.launch {
            if (playbackScreenState.value.isPlayingSong) {
                playMusic()
            } else {
                pauseMusic()
            }
        }
    }

    private suspend fun playMusic() {
        _mp3PlayerEvent.send(
            MP3PlayerEvent.PauseSong(
                playbackScreenState.value.mp3Items.firstOrNull()?.uri ?: Uri.parse("")
            )
        )
        _playbackScreenState.update {
            it.copy(
                playbackScreenEnum = PlaybackScreenEnum.SONG,
                isPlayingSong = false,
                songBeingPlayed = null
            )
        }
    }

    private suspend fun pauseMusic() {
        _mp3PlayerEvent.send(
            MP3PlayerEvent.PlaySong(
                playbackScreenState.value.mp3Items.firstOrNull()?.uri ?: Uri.parse("")
            )
        )
        _playbackScreenState.update {
            it.copy(
                playbackScreenEnum = PlaybackScreenEnum.SONG,
                isPlayingSong = true,
                songBeingPlayed = playbackScreenState.value.mp3Items.firstOrNull()
            )
        }
    }

    private fun handleMiddleButtonClicked(
        playbackScreenEnum: PlaybackScreenEnum,
        isMenuVisible: Boolean
    ) {
        when {
            playbackScreenEnum == PlaybackScreenEnum.HOME -> {
                //Maybe choose a certain file?
                //And:
                viewModelScope.launch {
                    _mp3PlayerEvent.send(MP3PlayerEvent.AccessMediaSingleFile)
                }
            }

            !isMenuVisible && playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST -> {
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
