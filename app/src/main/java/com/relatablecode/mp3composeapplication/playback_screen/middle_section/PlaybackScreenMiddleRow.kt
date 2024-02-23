package com.relatablecode.mp3composeapplication.playback_screen.middle_section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.relatablecode.mp3composeapplication.playback_screen.middle_section.home.PlaybackScreenHome
import com.relatablecode.mp3composeapplication.playback_screen.middle_section.music_list.MusicListState
import com.relatablecode.mp3composeapplication.playback_screen.middle_section.music_list.PlaybackScreenMusicList
import com.relatablecode.mp3composeapplication.playback_screen.middle_section.settings.PlaybackScreenSettings
import com.relatablecode.mp3composeapplication.playback_screen.middle_section.song.PlaybackScreenSong
import com.relatablecode.mp3composeapplication.playback_screen.middle_section.song.SongState
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum

@Composable
fun PlaybackScreenMiddleRow(
    modifier: Modifier = Modifier, middleRowState: MiddleRowState
) {

    val musicListState = remember(middleRowState.mp3Items) {
        MusicListState(mp3Items = middleRowState.mp3Items)
    }

    val songState = remember(middleRowState.songBeingPlayed) {
        SongState(songBeingPlayed = middleRowState.songBeingPlayed)
    }

    when(middleRowState.playbackScreenEnum) {
        PlaybackScreenEnum.MUSIC_LIST -> {
            PlaybackScreenMusicList(modifier = modifier, musicListState = musicListState)
        }

        PlaybackScreenEnum.SONG -> {
            PlaybackScreenSong(songState = songState)
        }

        PlaybackScreenEnum.SETTINGS -> {
            PlaybackScreenSettings()
        }

        else -> {
            PlaybackScreenHome()
        }
    }

}
