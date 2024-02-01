package com.relatablecode.mp3composeapplication.black_screen.middle_section

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.relatablecode.mp3composeapplication.black_screen.state.BlackScreenEnum
import com.relatablecode.mp3composeapplication.black_screen.state.BlackScreenState

@Composable
fun BlackScreenMiddleRow(
    modifier: Modifier = Modifier, blackScreenState: BlackScreenState = BlackScreenState(
        blackScreenEnum = BlackScreenEnum.HOME, isMenuVisible = true
    )
) {
    BlackScreenHome()
}
