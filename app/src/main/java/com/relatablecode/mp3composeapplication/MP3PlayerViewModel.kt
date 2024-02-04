package com.relatablecode.mp3composeapplication

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.relatablecode.mp3composeapplication.circular_control_panel.CircularControlClickEvent
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import com.relatablecode.mp3composeapplication.use_cases.MP3PlayerUseCases
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
    private val useCases: MP3PlayerUseCases
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
            useCases.getUrisUseCase().collect { uriStrings ->
                _uris.value = uriStrings.map { Uri.parse(it) }.toSet()
            }
        }
    }

    fun saveUri(uri: Uri) {
        viewModelScope.launch {
            useCases.saveUriUseCase(uri)
        }
    }

    //Created to save multiple uris when the user wants to import several mp3 files at once
    fun saveUris(uris: List<Uri>) {
        viewModelScope.launch {
            useCases.saveUrisUseCase(uris)
        }
    }

    fun deleteUri(uri: Uri) {
        viewModelScope.launch {
            useCases.deleteUriUseCase(uri)
        }
    }

    fun updateMp3Items(mp3Items: List<Mp3Item>) {
        viewModelScope.launch {
            useCases.updateMp3ItemsUseCase(
                state = _playbackScreenState,
                mp3Items = mp3Items
            )
        }
    }

    fun navigateToMusicList() {
        useCases.navigateToMusicListUseCase(state = _playbackScreenState)
    }

    fun onEvent(event: CircularControlClickEvent) {
        when (event) {
            CircularControlClickEvent.OnMenuClicked -> {
                useCases.menuButtonClickedUseCase(state = _playbackScreenState)
            }

            CircularControlClickEvent.OnRewindClicked -> {
                useCases.rewindButtonClickedUseCase(state = _playbackScreenState)
            }

            CircularControlClickEvent.OnFastForwardClicked -> {
                useCases.fastForwardButtonClickedUseCase(state = _playbackScreenState)
            }

            CircularControlClickEvent.OnPlayPauseClicked -> {
                viewModelScope.launch {
                    useCases.playPauseButtonClickedUseCase(
                        state = _playbackScreenState,
                        mp3PlayerEventChannel = _mp3PlayerEvent,
                        uri = playbackScreenState.value.mp3Items.firstOrNull()?.uri ?: Uri.parse("")
                    )
                }
            }

            CircularControlClickEvent.OnMiddleButtonClicked -> {
                viewModelScope.launch {
                    useCases.middleButtonClickedUseCase(
                        state = _playbackScreenState,
                        mp3PlayerEventChannel = _mp3PlayerEvent
                    )
                }
            }

            CircularControlClickEvent.Default -> {}
        }
    }

}
