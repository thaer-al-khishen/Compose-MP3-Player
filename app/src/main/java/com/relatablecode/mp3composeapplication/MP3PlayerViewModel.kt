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

            !isMenuVisible && playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST -> {
                val currentIndex = _playbackScreenState.value.mp3Items.indexOfFirst { it.isSelected }
                if (currentIndex != -1) { // If there's a selected item
                    // Calculate the previous index with circular list behavior
                    val previousIndex = if (currentIndex - 1 < 0) _playbackScreenState.value.mp3Items.size - 1 else currentIndex - 1
                    val updatedItems = _playbackScreenState.value.mp3Items.mapIndexed { index, item ->
                        item.copy(isSelected = index == previousIndex)
                    }
                    // Update the state with the newly selected item
                    _playbackScreenState.update { it.copy(mp3Items = updatedItems) }
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

            !isMenuVisible && playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST -> {
                //AppTodo(2): Go to the next song beneath this one
                // Find the index of the currently selected item
                val currentIndex = _playbackScreenState.value.mp3Items.indexOfFirst { it.isSelected }
                if (currentIndex != -1) { // If there's a selected item
                    val nextIndex = (currentIndex + 1) % _playbackScreenState.value.mp3Items.size
                    val updatedItems = _playbackScreenState.value.mp3Items.mapIndexed { index, item ->
                        item.copy(isSelected = index == nextIndex)
                    }
                    // Update the state with the newly selected item
                    _playbackScreenState.update { it.copy(mp3Items = updatedItems) }
                }
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
            MP3PlayerEvent.PauseSong
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
                viewModelScope.launch {
                    _mp3PlayerEvent.send(MP3PlayerEvent.AccessMediaSingleFile)
                }
            }

            isMenuVisible && playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST -> {
                //AppTodo(4): Play the currently selected song and navigate to the play song screen
                //When you are in this music screen and you click on the middle button
                //First the menu will get hidden, next, if no items are currently selected, the first mp3item will get selected by default
                //We will add some logic to scroll down to the currently selected mp3item
                _playbackScreenState.update {
                    it.copy(isMenuVisible = false, mp3Items = it.mp3Items.also { mp3Items ->
                        (mp3Items.firstOrNull { it.isSelected })?.let {} ?: run {
                            mp3Items[0].isSelected = true
                        }
                    })
                }
            }

            !isMenuVisible && playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST -> {
                //AppTodo(4): Play the currently selected song and navigate to the play song screen
                //And:
                viewModelScope.launch {
                    //If there is no song currently playing, play the selected song
                    if (!_playbackScreenState.value.isPlayingSong) {
                        _mp3PlayerEvent.send(
                            MP3PlayerEvent.PlaySong(
                                //If there is a song that's selected, play that song, else, play the first song if it exists, else, Uri.parse("")
                                playbackScreenState.value.mp3Items.firstOrNull { it.isSelected }?.uri ?:
                                playbackScreenState.value.mp3Items.firstOrNull()?.uri ?:
                                Uri.parse("")
                            )
                        )
                    } else {
                        //If there is a song currently playing, pause it and play the selected song
                        _mp3PlayerEvent.send(
                            MP3PlayerEvent.PauseSong
                        )
                        _mp3PlayerEvent.send(
                            MP3PlayerEvent.PlaySong(
                                //If there is a song that's selected, play that song, else, play the first song if it exists, else, Uri.parse("")
                                playbackScreenState.value.mp3Items.firstOrNull { it.isSelected }?.uri ?: playbackScreenState.value.mp3Items.firstOrNull()?.uri ?: Uri.parse("")
                            )
                        )
                    }
                    _playbackScreenState.update {
                        it.copy(
                            isMenuVisible = true,
                            playbackScreenEnum = PlaybackScreenEnum.SONG,
                            isPlayingSong = true,
                            songBeingPlayed = playbackScreenState.value.mp3Items.firstOrNull { it.isSelected }
                        )
                    }
                }
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
