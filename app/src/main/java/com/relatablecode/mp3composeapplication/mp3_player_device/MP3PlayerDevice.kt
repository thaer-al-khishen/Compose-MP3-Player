package com.relatablecode.mp3composeapplication.mp3_player_device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.relatablecode.mp3composeapplication.black_screen.BlackScreen
import com.relatablecode.mp3composeapplication.circular_control_panel.CircularControlPanel

@Composable
fun MP3PlayerDevice() {

    ConstraintLayout(
        modifier = Modifier
            .background(brush = MP3PlayerDeviceBackground())
            .fillMaxSize()
    ) {
        val guideline = createGuidelineFromTop(0.5f)    //Creates a guideline at 50% of the screen from top to bottom

        // Screen with rounded corners
        val (screen, buttons) = createRefs()

        BlackScreen(modifier = Modifier.constrainAs(screen) {
            top.linkTo(parent.top, 20.dp)
            bottom.linkTo(guideline)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })

        CircularControlPanel(modifier = Modifier.constrainAs(buttons) {
            top.linkTo(guideline, 20.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })

    }

}
