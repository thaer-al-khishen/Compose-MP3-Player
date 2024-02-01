package com.relatablecode.mp3composeapplication.mp3_player_device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import com.relatablecode.mp3composeapplication.black_screen.BlackScreen

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
            top.linkTo(parent.top)
            bottom.linkTo(guideline)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })

        // Placeholder for MP3 buttons below the guideline
        Button(
            onClick = {  },
            modifier = Modifier.constrainAs(buttons) {
                top.linkTo(guideline)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text(text = "Play")
        }

    }

}
