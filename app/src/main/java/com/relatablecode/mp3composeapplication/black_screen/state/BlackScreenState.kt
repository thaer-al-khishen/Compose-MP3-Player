package com.relatablecode.mp3composeapplication.black_screen.state

data class BlackScreenState(
    var blackScreenEnum: BlackScreenEnum,
    var isMenuVisible: Boolean = true
)
