package com.relatablecode.mp3composeapplication.event

import android.net.Uri

sealed class MP3PlayerSongEvent {
    data class PlaySong(val uri: Uri): MP3PlayerSongEvent()
    data class ResumeSong(val uri: Uri): MP3PlayerSongEvent()
    object PauseSong: MP3PlayerSongEvent()
    object StopSong: MP3PlayerSongEvent()
    object ShowDeleteSongUI: MP3PlayerSongEvent()
}
