package com.relatablecode.mp3composeapplication.event_broadcaster

interface SongListener {
    fun onSongReceived(songTitle: String?)
}