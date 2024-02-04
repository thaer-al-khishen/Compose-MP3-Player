package com.relatablecode.mp3composeapplication.use_cases.controls

import android.net.Uri
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import com.relatablecode.mp3composeapplication.use_cases.music.PauseMusicUseCase
import com.relatablecode.mp3composeapplication.use_cases.music.PlayMusicUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MiddleButtonClickedUseCase @Inject constructor(
    private val playMusicUseCase: PlayMusicUseCase, private val pauseMusicUseCase: PauseMusicUseCase
) {

    suspend operator fun invoke(
        state: MutableStateFlow<PlaybackScreenState>, mp3PlayerEventChannel: Channel<MP3PlayerEvent>
    ) {

        val isInsideHome = state.value.playbackScreenEnum == PlaybackScreenEnum.HOME
        val isInsideMusicWithMenu =
            state.value.isMenuVisible && state.value.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST
        val isInsideMusicWithoutMenu =
            !state.value.isMenuVisible && state.value.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST

        when {
            isInsideHome -> {
                //Maybe choose a certain file?
                sendAccessMediaEvent(mp3PlayerEventChannel)
            }

            isInsideMusicWithMenu -> {
                //When you are in this music screen and you click on the middle button
                //First the menu will get hidden, next, if no items are currently selected, the first mp3item will get selected by default
                hideMenuAndHandleSongSelection(
                    state = state,
                    mp3PlayerEventChannel = mp3PlayerEventChannel
                )
            }

            isInsideMusicWithoutMenu -> {
                //Play the currently selected song and navigate to the play song screen
                handleClickInsideMusicListWithoutMenu(
                    state = state, mp3PlayerEventChannel = mp3PlayerEventChannel
                )
            }

            else -> {
                showMenu(state)
            }
        }

    }

    private suspend fun sendAccessMediaEvent(mp3PlayerEventChannel: Channel<MP3PlayerEvent>) {
        mp3PlayerEventChannel.send(MP3PlayerEvent.AccessMediaMultipleFiles)
    }

    private suspend fun hideMenuAndHandleSongSelection(
        state: MutableStateFlow<PlaybackScreenState>,
        mp3PlayerEventChannel: Channel<MP3PlayerEvent>
    ) {
        //If there are no songs available yet, prompt the user to import them
        if (state.value.mp3Items.isEmpty()) {
            sendAccessMediaEvent(mp3PlayerEventChannel)
        } else {
            //Select first song by default if no song is selected yet
            state.update {
                it.copy(isMenuVisible = false, mp3Items = it.mp3Items.also { mp3Items ->
                    (mp3Items.firstOrNull { it.isSelected })?.let {} ?: run {
                        mp3Items[0].isSelected = true
                    }
                })
            }
        }
    }

    private suspend fun handleClickInsideMusicListWithoutMenu(
        state: MutableStateFlow<PlaybackScreenState>, mp3PlayerEventChannel: Channel<MP3PlayerEvent>
    ) {
        if (!state.value.isPlayingSong) {
            playSongDirectly(
                state = state,
                mp3PlayerEventChannel = mp3PlayerEventChannel,
                uri = state.value.mp3Items.firstOrNull { it.isSelected }?.uri
                    ?: state.value.mp3Items.firstOrNull()?.uri ?: Uri.parse("")
            )
        } else {
            //If there is a song currently playing, pause it and play the selected song
            pauseThenPlaySong(
                state = state,
                mp3PlayerEventChannel = mp3PlayerEventChannel,
                uri = state.value.mp3Items.firstOrNull { it.isSelected }?.uri
                    ?: state.value.mp3Items.firstOrNull()?.uri ?: Uri.parse("")
            )
        }
    }

    private suspend fun playSongDirectly(
        state: MutableStateFlow<PlaybackScreenState>,
        mp3PlayerEventChannel: Channel<MP3PlayerEvent>,
        uri: Uri
    ) {
        playMusicUseCase.invoke(
            state = state, mp3PlayerEventChannel = mp3PlayerEventChannel, uri = uri
        )
    }

    private suspend fun pauseThenPlaySong(
        state: MutableStateFlow<PlaybackScreenState>,
        mp3PlayerEventChannel: Channel<MP3PlayerEvent>,
        uri: Uri
    ) {
        pauseMusicUseCase.invoke(
            state = state,
            mp3PlayerEventChannel = mp3PlayerEventChannel,
        )
        playMusicUseCase.invoke(
            state = state, mp3PlayerEventChannel = mp3PlayerEventChannel, uri = uri
        )
    }

    private fun showMenu(state: MutableStateFlow<PlaybackScreenState>) {
        state.update { it.copy(isMenuVisible = true) }
    }

}
