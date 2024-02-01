package com.relatablecode.mp3composeapplication.playback_screen.middle_section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.Theme

@Composable
fun PlaybackScreenHome(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.then(
            Modifier.fillMaxWidth()
        ),
        verticalArrangement = Arrangement.Center,  //Divides the content equally, like SpaceEvenly, but in this case, the start and end composables inside the content are at the edges of the screen
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_music_folder),
            tint = Theme.PlaybackScreenMiddleImageColor,
            contentDescription = "Music Folder",
        )
        Text(text = "Music Player", color = Theme.PlaybackScreenContentColor, fontSize = 16.sp)
    }
}