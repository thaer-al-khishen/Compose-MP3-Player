package com.relatablecode.mp3composeapplication.black_screen.bottom_section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.relatablecode.mp3composeapplication.R

@Composable
fun BlackScreenBottomRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.then(
            Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ),
        horizontalArrangement = Arrangement.Start,  //Divides the content equally, like SpaceEvenly, but in this case, the start and end composables inside the content are at the edges of the screen
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MP3PlayerSelectionIcon(
            isSelected = true,
            selectedImage = R.drawable.ic_home_blue,
            unSelectedImage = R.drawable.ic_home_white,
            contentDescription = "Home"
        )
        Spacer(modifier = Modifier.width(8.dp))
        MP3PlayerSelectionIcon(
            isSelected = false,
            selectedImage = R.drawable.ic_music_note_blue,
            unSelectedImage = R.drawable.ic_music_note_white,
            contentDescription = "Home"
        )
    }
}
