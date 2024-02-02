package com.relatablecode.mp3composeapplication.event

import android.net.Uri

sealed class MP3PlayerEvent {
    object AccessMediaSingleFile: MP3PlayerEvent()
    object AccessMediaMultipleFiles: MP3PlayerEvent()
    data class PlaySong(val uri: Uri): MP3PlayerEvent()
    data class PauseSong(val uri: Uri): MP3PlayerEvent()
}
