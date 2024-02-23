package com.relatablecode.mp3composeapplication.playback_screen.bottom_section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenEnum
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState

@Composable
fun PlaybackScreenBottomRow(modifier: Modifier = Modifier, bottomRowState: BottomRowState) {  //You need isMenuVisible and playbackScreenEnum
    Row(
        modifier = modifier.then(
            Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ),
        horizontalArrangement = Arrangement.Start,  //Divides the content equally, like SpaceEvenly, but in this case, the start and end composables inside the content are at the edges of the screen
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (bottomRowState.isMenuVisible) {
            MP3PlayerSelectionIcon(
                isSelected = bottomRowState.playbackScreenEnum == PlaybackScreenEnum.HOME,
                selectedImage = R.drawable.ic_home_blue,
                unSelectedImage = R.drawable.ic_home_white,
                contentDescription = "Home"
            )
            Spacer(modifier = Modifier.width(8.dp))
            MP3PlayerSelectionIcon(
                isSelected = bottomRowState.playbackScreenEnum == PlaybackScreenEnum.MUSIC_LIST,
                selectedImage = R.drawable.ic_music_note_blue,
                unSelectedImage = R.drawable.ic_music_note_white,
                contentDescription = "Home"
            )
            Spacer(modifier = Modifier.width(8.dp))
            MP3PlayerSelectionIcon(
                isSelected = bottomRowState.playbackScreenEnum == PlaybackScreenEnum.SONG,
                selectedImage = R.drawable.ic_play,
                unSelectedImage = R.drawable.ic_play,
                contentDescription = "Home"
            )
            Spacer(modifier = Modifier.width(8.dp))
            MP3PlayerSelectionIcon(
                isSelected = bottomRowState.playbackScreenEnum == PlaybackScreenEnum.SETTINGS,
                selectedImage = R.drawable.ic_settings,
                unSelectedImage = R.drawable.ic_settings,
                contentDescription = "Home"
            )
        } else {
            Spacer(modifier = Modifier.height(24.dp))   //24dp is the size of the image used inside MP3PlayerSelectionIcon
        }
    }
}
