package com.relatablecode.mp3composeapplication.mp3_player_device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.exoplayer.ExoPlayer
import com.relatablecode.mp3composeapplication.circular_control_panel.CircularControlClickEvent
import com.relatablecode.mp3composeapplication.playback_screen.PlaybackScreen
import com.relatablecode.mp3composeapplication.circular_control_panel.CircularControlPanel
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState

@Composable
fun MP3PlayerDevice(playbackScreenState: PlaybackScreenState, onEvent: (CircularControlClickEvent) -> Unit) {

    ConstraintLayout(
        modifier = Modifier
            .background(brush = mp3PlayerDeviceBackground())
            .fillMaxSize()
    ) {
        val guideline = createGuidelineFromTop(0.5f)    //Creates a guideline at 50% of the screen from top to bottom

        // Screen with rounded corners
        val (screen, buttons) = createRefs()

        PlaybackScreen(modifier = Modifier.constrainAs(screen) {
            top.linkTo(parent.top, 20.dp)
            bottom.linkTo(guideline)
            start.linkTo(parent.start, 16.dp)
            end.linkTo(parent.end, 16.dp)
            width = Dimension.fillToConstraints
            height = Dimension.value(300.dp)
        }, playbackScreenState = playbackScreenState)

        CircularControlPanel(modifier = Modifier.constrainAs(buttons) {
            top.linkTo(guideline, 50.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }, onEvent)

    }

}
