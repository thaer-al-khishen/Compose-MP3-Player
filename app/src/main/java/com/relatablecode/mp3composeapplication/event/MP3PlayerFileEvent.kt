package com.relatablecode.mp3composeapplication.event

sealed class MP3PlayerFileEvent {
    object AccessMediaMultipleFiles: MP3PlayerFileEvent()
}
