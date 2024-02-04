package com.relatablecode.mp3composeapplication

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.relatablecode.mp3composeapplication.circular_control_panel.CircularControlClickEvent
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import com.relatablecode.mp3composeapplication.use_cases.controls.FastForwardButtonClickedUseCase
import com.relatablecode.mp3composeapplication.use_cases.controls.MenuButtonClickedUseCase
import com.relatablecode.mp3composeapplication.use_cases.controls.MiddleButtonClickedUseCase
import com.relatablecode.mp3composeapplication.use_cases.controls.PlayPauseButtonClickedUseCase
import com.relatablecode.mp3composeapplication.use_cases.controls.RewindButtonClickedUseCase
import com.relatablecode.mp3composeapplication.use_cases.general.NavigateToMusicListUseCase
import com.relatablecode.mp3composeapplication.use_cases.general.UpdateMp3ItemsUseCase
import com.relatablecode.mp3composeapplication.use_cases.uri.DeleteUriUseCase
import com.relatablecode.mp3composeapplication.use_cases.uri.GetUrisUseCase
import com.relatablecode.mp3composeapplication.use_cases.uri.SaveUriUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MP3PlayerViewModel @Inject constructor(
    private val getUrisUseCase: GetUrisUseCase,
    private val saveUriUseCase: SaveUriUseCase,
    private val deleteUriUseCase: DeleteUriUseCase,
    private val menuButtonClickedUseCase: MenuButtonClickedUseCase,
    private val rewindButtonClickedUseCase: RewindButtonClickedUseCase,
    private val fastForwardButtonClickedUseCase: FastForwardButtonClickedUseCase,
    private val playPauseButtonClickedUseCase: PlayPauseButtonClickedUseCase,
    private val middleButtonClickedUseCase: MiddleButtonClickedUseCase,
    private val updateMp3ItemsUseCase: UpdateMp3ItemsUseCase,
    private val navigateToMusicListUseCase: NavigateToMusicListUseCase
) : ViewModel() {

    private val _playbackScreenState = MutableStateFlow(
        PlaybackScreenState(
            playbackScreenEnum = PlaybackScreenEnum.HOME,
            isMenuVisible = true,
        )
    )
    val playbackScreenState: StateFlow<PlaybackScreenState> = _playbackScreenState

    private val _mp3PlayerEvent = Channel<MP3PlayerEvent>(Channel.BUFFERED)
    val mp3PlayerEvent = _mp3PlayerEvent.receiveAsFlow()

    private val _uris = MutableStateFlow<Set<Uri>>(emptySet())
    val uris: StateFlow<Set<Uri>> = _uris.asStateFlow()

    init {
        collectUris()
    }

    private fun collectUris() {
        viewModelScope.launch {
            getUrisUseCase().collect { uriStrings ->
                _uris.value = uriStrings.map { Uri.parse(it) }.toSet()
            }
        }
    }

    fun saveUri(uri: Uri) {
        viewModelScope.launch {
            saveUriUseCase(uri)
        }
    }

    fun deleteUri(uri: Uri) {
        viewModelScope.launch {
            deleteUriUseCase(uri)
        }
    }

    fun updateMp3Items(mp3Items: List<Mp3Item>) {
        viewModelScope.launch {
            updateMp3ItemsUseCase(
                state = _playbackScreenState,
                mp3Items = mp3Items
            )
        }
    }

    fun navigateToMusicList() {
        navigateToMusicListUseCase(state = _playbackScreenState)
    }

    fun onEvent(event: CircularControlClickEvent) {
        when (event) {
            CircularControlClickEvent.OnMenuClicked -> {
                menuButtonClickedUseCase(state = _playbackScreenState)
            }

            CircularControlClickEvent.OnRewindClicked -> {
                rewindButtonClickedUseCase(state = _playbackScreenState)
            }

            CircularControlClickEvent.OnFastForwardClicked -> {
                fastForwardButtonClickedUseCase(state = _playbackScreenState)
            }

            CircularControlClickEvent.OnPlayPauseClicked -> {
                viewModelScope.launch {
                    playPauseButtonClickedUseCase(
                        state = _playbackScreenState,
                        mp3PlayerEventChannel = _mp3PlayerEvent,
                        uri = playbackScreenState.value.mp3Items.firstOrNull()?.uri ?: Uri.parse("")
                    )
                }
            }

            CircularControlClickEvent.OnMiddleButtonClicked -> {
                viewModelScope.launch {
                    middleButtonClickedUseCase(
                        state = _playbackScreenState,
                        mp3PlayerEventChannel = _mp3PlayerEvent
                    )
                }
            }

            CircularControlClickEvent.Default -> {}
        }
    }

}
