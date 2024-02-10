package com.relatablecode.mp3composeapplication.circular_control_panel

sealed class CircularControlClickEvent {
    object OnMenuClicked: CircularControlClickEvent()
    object OnRewindClicked: CircularControlClickEvent()
    object OnFastForwardClicked: CircularControlClickEvent()
    object OnPlayPauseClicked: CircularControlClickEvent()
    object OnMiddleButtonClicked: CircularControlClickEvent()
    object OnMiddleButtonLongClicked: CircularControlClickEvent()
    object Default: CircularControlClickEvent()
}
