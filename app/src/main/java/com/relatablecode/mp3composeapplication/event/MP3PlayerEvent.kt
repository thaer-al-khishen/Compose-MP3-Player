package com.relatablecode.mp3composeapplication.event

sealed class MP3PlayerEvent {
    object AccessMediaSingleFile: MP3PlayerEvent()
    object AccessMediaMultipleFiles: MP3PlayerEvent()
}
