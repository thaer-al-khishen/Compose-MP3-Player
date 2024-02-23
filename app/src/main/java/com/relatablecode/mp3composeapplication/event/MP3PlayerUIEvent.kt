package com.relatablecode.mp3composeapplication.event

import com.relatablecode.mp3composeapplication.Mp3Item

//Triggered from the MainActivity after some action
sealed class MP3PlayerUIEvent {
    object DeleteSong: MP3PlayerUIEvent()   //Triggered after the user confirms deleting a song through an alert dialogue
    object NavigateToMusicList: MP3PlayerUIEvent()  //Triggered after the user chooses mp3 files from the file picker
    data class UpdateMp3Items(val mp3Items: List<Mp3Item>): MP3PlayerUIEvent()  //Triggered when the activity enters the started state
    object PlayNextSong: MP3PlayerUIEvent()
    object PlayPreviousSong: MP3PlayerUIEvent()
}
