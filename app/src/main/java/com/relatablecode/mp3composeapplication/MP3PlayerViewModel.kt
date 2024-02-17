package com.relatablecode.mp3composeapplication

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.relatablecode.mp3composeapplication.circular_control_panel.CircularControlClickEvent
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import com.relatablecode.mp3composeapplication.use_cases.MP3PlayerUseCases
import com.relatablecode.mp3composeapplication.use_cases.controls.MiddleButtonAction
import com.relatablecode.mp3composeapplication.use_cases.controls.MiddleButtonLongClickedAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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

    fun deleteSong() {
        viewModelScope.launch {
            val currentState = playbackScreenState.value
            val currentSongs = currentState.mp3Items
            val selectedSongIndex = currentSongs.indexOfFirst { it.isSelected }

            if (selectedSongIndex != -1) {
                // Determine the index for new selection
                val newSelectedIndex = when {
                    currentSongs.size == 1 -> -1 // If only one song, no selection after deletion
                    selectedSongIndex == 0 -> 0 // If the first song is selected and deleted, select the next song which will move to the first position
                    else -> selectedSongIndex - 1 // Otherwise, select the previous song
                }

                // Delete the selected song URI from the repository
                val uriToDelete = currentSongs[selectedSongIndex].uri
                useCases.deleteUriUseCase(uriToDelete)

                // Wait for the deletion to reflect in the observed URIs list
                delay(100) // This delay is hypothetical and depends on how quickly your app can process URI deletions

                // Update the list of songs without the deleted one and apply the new selection
                val updatedSongsWithoutDeleted = currentState.mp3Items.filter { it.uri != uriToDelete }
                val updatedSongsWithNewSelection = updatedSongsWithoutDeleted.mapIndexed { index, item ->
                    item.copy(isSelected = index == newSelectedIndex)
                }

                // Update state with the new song list and selection
                _playbackScreenState.update { it.copy(mp3Items = updatedSongsWithNewSelection) }
            }
        }
    }

    fun updateMp3Items(mp3Items: List<Mp3Item>) {
        val newState =
            useCases.updateMp3ItemsUseCase(state = playbackScreenState.value, mp3Items = mp3Items)
        _playbackScreenState.update { newState }
    }

    fun navigateToMusicList() {
        val newState = useCases.navigateToMusicListUseCase(state = playbackScreenState.value)
        _playbackScreenState.update { newState }
    }

    fun onEvent(event: CircularControlClickEvent) {
        when (event) {
            CircularControlClickEvent.OnMenuClicked -> {
                handleMenuButtonClicked()
            }

            CircularControlClickEvent.OnRewindClicked -> {
                handleRewindButtonClicked()
            }

            CircularControlClickEvent.OnFastForwardClicked -> {
                handleFastForwardButtonClicked()
            }

            CircularControlClickEvent.OnPlayPauseClicked -> {
                handlePlayPauseButtonClicked()
            }

            CircularControlClickEvent.OnMiddleButtonClicked -> {
                handleMiddleButtonClicked()
            }

            CircularControlClickEvent.OnMiddleButtonLongClicked -> {
                val action =
                    useCases.middleButtonLongClickedUseCase(currentState = playbackScreenState.value)
                when (action) {
                    MiddleButtonLongClickedAction.DELETE_SONG -> {
                        viewModelScope.launch {
                            _mp3PlayerEvent.send(MP3PlayerEvent.ShowDeleteSongUI)
                        }
                    }

                    else -> {}
                }
            }

            CircularControlClickEvent.Default -> {}

        }

    }

    private fun handleMiddleButtonClicked() {
        viewModelScope.launch {
            val action = useCases.middleButtonClickedUseCase(playbackScreenState.value)
            when (action) {
                MiddleButtonAction.AccessMedia -> _mp3PlayerEvent.send(MP3PlayerEvent.AccessMediaMultipleFiles)
                MiddleButtonAction.HideMenuSelectFirstSong -> {
                    // Logic to hide the menu and select the first song, if any
                    if (_playbackScreenState.value.mp3Items.isEmpty()) {
                        _mp3PlayerEvent.send(MP3PlayerEvent.AccessMediaMultipleFiles)
                    } else {
                        //Select first song by default if no song is selected yet
                        _playbackScreenState.update {
                            it.copy(
                                isMenuVisible = false,
                                mp3Items = it.mp3Items.also { mp3Items ->
                                    (mp3Items.firstOrNull { it.isSelected })?.let {}
                                        ?: run {
                                            mp3Items[0].isSelected = true
                                        }
                                })
                        }
                    }
                }

                MiddleButtonAction.PlaySelectedSong -> {
                    // Logic to play the selected song or navigate to the song screen
                    playbackScreenState.value.mp3Items.firstOrNull { it.isSelected }?.let {
                        _playbackScreenState.update {
                            it.copy(
                                isMenuVisible = false,
                                mp3Items = it.mp3Items.also { mp3Items ->
                                    (mp3Items.firstOrNull { it.isSelected })?.let {}
                                        ?: run {
                                            mp3Items[0].isSelected = true
                                        }
                                })
                        }
                    }
                    if (!playbackScreenState.value.isPlayingSong) {
                        playMusic(
                            playbackScreenState.value.mp3Items.find { it.isSelected }?.uri
                        )
                    } else {
                        //If there is a song currently playing, pause it and play the selected song
                        pauseMusic()
                        playMusic(
                            playbackScreenState.value.mp3Items.find { it.isSelected }?.uri
                        )
                    }
                }

                MiddleButtonAction.ShowMenu -> _playbackScreenState.update {
                    it.copy(
                        isMenuVisible = true
                    )
                }

                MiddleButtonAction.HideMenu -> _playbackScreenState.update {
                    it.copy(
                        isMenuVisible = false
                    )
                }
            }
        }
    }

    private fun handlePlayPauseButtonClicked() {
        viewModelScope.launch {
            val playPauseMusicResult = useCases.playPauseButtonClickedUseCase(
                playbackScreenState.value,
                playbackScreenState.value.songBeingPlayed?.uri
                    ?: playbackScreenState.value.mp3Items.firstOrNull()?.uri
            )
            if (playPauseMusicResult.first) {
                playbackScreenState.value.songBeingPlayed?.let {
                    //Resume
                    resumeMusic(playPauseMusicResult.second)
                } ?: run {

                    //Check if this is the first song being played and triggered with the OnPlayPauseClicked button
                    //In this case select the first song
                    playbackScreenState.value.songBeingPlayed ?: run {
                        playbackScreenState.value.mp3Items[0].isSelected = true
                    }

                    //Play
                    playMusic(playPauseMusicResult.second)
                }
            } else {
                pauseMusic()
            }
        }
    }

    private fun handleFastForwardButtonClicked() {
        viewModelScope.launch {
            //Check if the user is trying to fast forward a song, or just navigate the menu
            val currentState = playbackScreenState.value
            val newState = useCases.fastForwardButtonClickedUseCase(currentState)
            if (currentState.playbackScreenEnum == PlaybackScreenEnum.SONG && !currentState.isMenuVisible) {
                playNextOrPreviousSong(newState)
            } else {
                _playbackScreenState.update { newState }
            }
        }
    }

    private fun handleRewindButtonClicked() {
        viewModelScope.launch {
            //Check if the user is trying to fast forward a song, or just navigate the menu
            val currentState = playbackScreenState.value
            val newState = useCases.rewindButtonClickedUseCase(currentState)
            if (currentState.playbackScreenEnum == PlaybackScreenEnum.SONG && !currentState.isMenuVisible) {
                playNextOrPreviousSong(newState)
            } else {
                _playbackScreenState.update { newState }
            }
        }
    }

    suspend fun playPreviousSong() {
        val currentIndex = playbackScreenState.value.mp3Items.indexOfFirst { it.isSelected }
        val previousIndex = if (currentIndex > 0) currentIndex - 1 else playbackScreenState.value.mp3Items.lastIndex // Wrap to the last song if at the beginning
        val updatedMp3Items = playbackScreenState.value.mp3Items.mapIndexed { index, mp3Item ->
            mp3Item.copy(isSelected = index == previousIndex)
        }
        val previousSongUri = updatedMp3Items.getOrNull(previousIndex)?.uri ?: return

        pauseMusic()
        playMusic(previousSongUri)
        _playbackScreenState.update {
            it.copy(mp3Items = updatedMp3Items, songBeingPlayed = updatedMp3Items[previousIndex])
        }
    }

    suspend fun playNextSong() {
        val currentIndex = playbackScreenState.value.mp3Items.indexOfFirst { it.isSelected }
        val nextIndex = if (currentIndex < playbackScreenState.value.mp3Items.lastIndex) currentIndex + 1 else 0 // Wrap to the first song if at the end
        val updatedMp3Items = playbackScreenState.value.mp3Items.mapIndexed { index, mp3Item ->
            mp3Item.copy(isSelected = index == nextIndex)
        }
        val nextSongUri = updatedMp3Items.getOrNull(nextIndex)?.uri ?: return

        pauseMusic()
        playMusic(nextSongUri)
        _playbackScreenState.update {
            it.copy(mp3Items = updatedMp3Items, songBeingPlayed = updatedMp3Items[nextIndex])
        }
    }

    private fun handleMenuButtonClicked() {
        val newIsMenuVisible =
            useCases.menuButtonClickedUseCase(playbackScreenState.value.isMenuVisible)
        _playbackScreenState.update { currentState ->
            currentState.copy(isMenuVisible = newIsMenuVisible)
        }
    }

    private suspend fun playMusic(uri: Uri?) {
        uri?.let {
            _mp3PlayerEvent.send(MP3PlayerEvent.PlaySong(uri))
            _playbackScreenState.update { currentState ->
                currentState.copy(isMenuVisible = true,
                    playbackScreenEnum = PlaybackScreenEnum.SONG,
                    isPlayingSong = true,
                    songBeingPlayed = currentState.mp3Items.find { it.uri == uri })
            }
        }
    }

    private suspend fun resumeMusic(uri: Uri?) {
        uri?.let {
            _mp3PlayerEvent.send(MP3PlayerEvent.ResumeSong(uri))
            _playbackScreenState.update { currentState ->
                currentState.copy(isMenuVisible = true,
                    playbackScreenEnum = PlaybackScreenEnum.SONG,
                    isPlayingSong = true,
                    songBeingPlayed = currentState.mp3Items.find { it.uri == uri })
            }
        }
    }

    private suspend fun pauseMusic() {
        _mp3PlayerEvent.send(MP3PlayerEvent.PauseSong)
        _playbackScreenState.update {
            it.copy(
                playbackScreenEnum = PlaybackScreenEnum.SONG,
                isPlayingSong = false
            )
        }
    }

    //Triggered by the rewind and fast forward button clicked inside the song UI without a menu
    private suspend fun playNextOrPreviousSong(state: PlaybackScreenState) {
        state.mp3Items.firstOrNull { it.isSelected }?.uri?.let { selectedUri ->
            pauseMusic()
            playMusic(selectedUri)
            state.songBeingPlayed = state.mp3Items.find { it.uri == selectedUri }
            _playbackScreenState.update { state }
        }
    }

}
