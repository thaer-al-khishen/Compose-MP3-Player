package com.relatablecode.mp3composeapplication

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.relatablecode.mp3composeapplication.circular_control_panel.CircularControlClickEvent
import com.relatablecode.mp3composeapplication.event.MP3PlayerSongEvent
import com.relatablecode.mp3composeapplication.event.MP3PlayerFileEvent
import com.relatablecode.mp3composeapplication.event.MP3PlayerThemeEvent
import com.relatablecode.mp3composeapplication.event.MP3PlayerUIEvent
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

    private val _mp3PlayerSongEvent = Channel<MP3PlayerSongEvent>(Channel.BUFFERED)
    val mp3PlayerEvent = _mp3PlayerSongEvent.receiveAsFlow()

    private val _mp3PlayerFileEvent = Channel<MP3PlayerFileEvent>(Channel.BUFFERED)
    val mp3PlayerFileEvent = _mp3PlayerFileEvent.receiveAsFlow()

    private val _mp3PlayerThemeEvent = Channel<MP3PlayerThemeEvent>(Channel.BUFFERED)
    val mp3PlayerThemeEvent = _mp3PlayerThemeEvent.receiveAsFlow()

    fun onGeneralUIEvent(event: MP3PlayerUIEvent) {
        when (event) {
            is MP3PlayerUIEvent.DeleteSong -> deleteSong()
            is MP3PlayerUIEvent.UpdateMp3Items -> updateMp3Items(event.mp3Items)
            is MP3PlayerUIEvent.NavigateToMusicList -> navigateToMusicList()
            is MP3PlayerUIEvent.PlayPreviousSong -> viewModelScope.launch { playPreviousSong() }
            is MP3PlayerUIEvent.PlayNextSong -> viewModelScope.launch { playNextSong() }
        }
    }

    private fun deleteSong() {
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

                //Make sure to handle the current played song being deleted
                if (playbackScreenState.value.songBeingPlayed == currentSongs[selectedSongIndex]) {
                    _mp3PlayerSongEvent.send(MP3PlayerSongEvent.StopSong)
                    _playbackScreenState.update {
                        it.copy(isPlayingSong = false, songBeingPlayed = null)
                    }
                }

                useCases.deleteUriUseCase(uriToDelete)

                // Wait for the deletion to reflect in the observed URIs list
                delay(100) // This delay is hypothetical and depends on how quickly your app can process URI deletions

                // Update the list of songs without the deleted one and apply the new selection
                val updatedSongsWithoutDeleted =
                    currentState.mp3Items.filter { it.uri != uriToDelete }
                val updatedSongsWithNewSelection =
                    updatedSongsWithoutDeleted.mapIndexed { index, item ->
                        item.copy(isSelected = index == newSelectedIndex)
                    }

                // Update state with the new song list and selection
                _playbackScreenState.update { it.copy(mp3Items = updatedSongsWithNewSelection) }
            }
        }
    }

    private fun updateMp3Items(mp3Items: List<Mp3Item>) {
        val newState =
            useCases.updateMp3ItemsUseCase(state = playbackScreenState.value, mp3Items = mp3Items)
        _playbackScreenState.update { newState }
    }

    private fun navigateToMusicList() {
        val newState = useCases.navigateToMusicListUseCase(state = playbackScreenState.value)
        _playbackScreenState.update { newState }
    }

    fun onCircularControlClickedEvent(circularControlClickEvent: CircularControlClickEvent) {
        when (circularControlClickEvent) {
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
                handleMiddleButtonLongClicked()
            }

            CircularControlClickEvent.Default -> {}

        }
    }

    private fun handleMiddleButtonClicked() {
        viewModelScope.launch {
            val action = useCases.middleButtonClickedUseCase(playbackScreenState.value)
            when (action) {
                MiddleButtonAction.AccessMedia -> _mp3PlayerFileEvent.send(MP3PlayerFileEvent.AccessMediaMultipleFiles)
                MiddleButtonAction.HideMenuSelectFirstSong -> {
                    // If no songs are available, prompt the file choosing launcher
                    if (_playbackScreenState.value.mp3Items.isEmpty()) {
                        _mp3PlayerFileEvent.send(MP3PlayerFileEvent.AccessMediaMultipleFiles)
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
                    // Logic to play the selected song
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
                    //If no song is being played, play the new song, else, pause it, and play the new song
                    if (!playbackScreenState.value.isPlayingSong) {
                        playMusic(
                            playbackScreenState.value.mp3Items.find { it.isSelected }?.uri
                        )
                    } else {
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

    private fun handleMiddleButtonLongClicked() {
        val action = useCases.middleButtonLongClickedUseCase(currentState = playbackScreenState.value)
        when (action) {
            MiddleButtonLongClickedAction.DELETE_SONG -> {
                viewModelScope.launch {
                    _mp3PlayerSongEvent.send(MP3PlayerSongEvent.ShowDeleteSongUI)
                }
            }

            else -> {}
        }
    }

    private fun handlePlayPauseButtonClicked() {
        viewModelScope.launch {

            val playPauseMusicResult = useCases.playPauseButtonClickedUseCase(
                playbackScreenState.value,
                playbackScreenState.value.songBeingPlayed?.uri?.let {
                    it
                } ?: run {
                    playbackScreenState.value.mp3Items.firstOrNull { it.isSelected }?.let {
                        it.uri
                    } ?: run {
                        playbackScreenState.value.mp3Items.firstOrNull()?.uri
                    }
                }
            )

            //playPauseMusicResult.first returns the new state of isPlayingSong, will be switched after clicking on this button
            //To pause/resume the song
            if (playPauseMusicResult.first) {
                //If there was already a song being played, resume it
                playbackScreenState.value.songBeingPlayed?.let {
                    resumeMusic(playPauseMusicResult.second)
                } ?: run {
                    //Check for the selected song, if any, and play it
                    //Else, if the list is not empty, play the first song and select it
                    //Else, if the list is empty, do nothing
                    val nextMp3Item =
                        playbackScreenState.value.mp3Items.firstOrNull { it.isSelected }?.let {
                            it
                        } ?: run {
                            if (playbackScreenState.value.mp3Items.isEmpty().not()) {
                                playbackScreenState.value.mp3Items[0].isSelected = true
                                playbackScreenState.value.mp3Items[0]
                            } else null
                        }

                    //Play the song
                    playMusic(nextMp3Item?.uri)
                }
            } else {
                //Pause the song in this case, since playPauseMusicResult.first is now false, requiring music to be stopped
                pauseMusic()
            }
        }
    }

    private fun handleFastForwardButtonClicked() {
        viewModelScope.launch {
            //Check if the user is trying to fast forward a song, change the theme, or just navigate the menu
            val currentState = playbackScreenState.value
            val newState = useCases.fastForwardButtonClickedUseCase(currentState)
            when {
                currentState.playbackScreenEnum == PlaybackScreenEnum.SONG && !currentState.isMenuVisible -> playNextOrPreviousSong(newState)
                currentState.playbackScreenEnum == PlaybackScreenEnum.SETTINGS && !currentState.isMenuVisible -> {
                    _mp3PlayerThemeEvent.send(MP3PlayerThemeEvent.SwitchToNextTheme)
                }
                else -> {
                    _playbackScreenState.update { newState }
                }
            }
        }
    }

    private fun handleRewindButtonClicked() {
        viewModelScope.launch {
            //Check if the user is trying to rewind a song, change the theme, or just navigate the menu
            val currentState = playbackScreenState.value
            val newState = useCases.rewindButtonClickedUseCase(currentState)
            when {
                currentState.playbackScreenEnum == PlaybackScreenEnum.SONG && !currentState.isMenuVisible -> playNextOrPreviousSong(newState)
                currentState.playbackScreenEnum == PlaybackScreenEnum.SETTINGS && !currentState.isMenuVisible -> {
                    _mp3PlayerThemeEvent.send(MP3PlayerThemeEvent.SwitchToPreviousTheme)
                }
                else -> {
                    _playbackScreenState.update { newState }
                }
            }
        }
    }

    //Triggered from the notification
    private suspend fun playPreviousSong() {
        val currentIndex = playbackScreenState.value.mp3Items.indexOfFirst { it.isSelected }
        val previousIndex =
            if (currentIndex > 0) currentIndex - 1 else playbackScreenState.value.mp3Items.lastIndex // Wrap to the last song if at the beginning
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

    //Triggered from the notification
    private suspend fun playNextSong() {
        val currentIndex = playbackScreenState.value.mp3Items.indexOfFirst { it.isSelected }
        val nextIndex =
            if (currentIndex < playbackScreenState.value.mp3Items.lastIndex) currentIndex + 1 else 0 // Wrap to the first song if at the end
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
        //Just show/hide the menu
        val newIsMenuVisible =
            useCases.menuButtonClickedUseCase(playbackScreenState.value.isMenuVisible)
        _playbackScreenState.update { currentState ->
            currentState.copy(isMenuVisible = newIsMenuVisible)
        }
    }

    private suspend fun playMusic(uri: Uri?) {
        uri?.let {
            _mp3PlayerSongEvent.send(MP3PlayerSongEvent.PlaySong(uri))
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
            _mp3PlayerSongEvent.send(MP3PlayerSongEvent.ResumeSong(uri))
            _playbackScreenState.update { currentState ->
                currentState.copy(isMenuVisible = true,
                    playbackScreenEnum = PlaybackScreenEnum.SONG,
                    isPlayingSong = true,
                    songBeingPlayed = currentState.mp3Items.find { it.uri == uri })
            }
        }
    }

    private suspend fun pauseMusic() {
        _mp3PlayerSongEvent.send(MP3PlayerSongEvent.PauseSong)
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
