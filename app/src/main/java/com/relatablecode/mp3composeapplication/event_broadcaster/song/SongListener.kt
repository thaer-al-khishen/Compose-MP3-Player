package com.relatablecode.mp3composeapplication.event_broadcaster.song

interface SongListener {
    fun onSongReceived(songTitle: String?)
}