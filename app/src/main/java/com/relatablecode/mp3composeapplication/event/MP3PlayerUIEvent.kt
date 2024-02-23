package com.relatablecode.mp3composeapplication.event

import com.relatablecode.mp3composeapplication.Mp3Item

sealed class MP3PlayerUIEvent {
    object DeleteSong: MP3PlayerUIEvent()
    object NavigateToMusicList: MP3PlayerUIEvent()
    data class UpdateMp3Items(val mp3Items: List<Mp3Item>): MP3PlayerUIEvent()
}
