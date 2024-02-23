package com.relatablecode.mp3composeapplication.event

import android.net.Uri

sealed class MP3PlayerEvent {
    //Take files to a separate sealed class
    object AccessMediaSingleFile: MP3PlayerEvent()
    object AccessMediaMultipleFiles: MP3PlayerEvent()
    data class PlaySong(val uri: Uri): MP3PlayerEvent()
    data class ResumeSong(val uri: Uri): MP3PlayerEvent()
    object PauseSong: MP3PlayerEvent()
    object StopSong: MP3PlayerEvent()
    object ShowDeleteSongUI: MP3PlayerEvent()
    //Take themes to a separate sealed class
    object SwitchToNextTheme: MP3PlayerEvent()
    object SwitchToPreviousTheme: MP3PlayerEvent()
}
