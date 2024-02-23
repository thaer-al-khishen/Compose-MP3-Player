package com.relatablecode.mp3composeapplication.event

sealed class MP3PlayerThemeEvent {
    object SwitchToNextTheme: MP3PlayerThemeEvent()
    object SwitchToPreviousTheme: MP3PlayerThemeEvent()
}
