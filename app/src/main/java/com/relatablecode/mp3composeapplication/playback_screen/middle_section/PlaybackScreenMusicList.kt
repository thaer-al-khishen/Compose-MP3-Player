package com.relatablecode.mp3composeapplication.playback_screen.middle_section

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relatablecode.mp3composeapplication.Mp3Item
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.Theme
import com.relatablecode.mp3composeapplication.conditional
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState

@Composable
fun PlaybackScreenMusicList(
    modifier: Modifier = Modifier,
    playbackScreenState: PlaybackScreenState
) {
    if (playbackScreenState.mp3Items.isEmpty()) {
        Column(
            modifier = modifier.then(
                Modifier.fillMaxWidth()
            ),
            verticalArrangement = Arrangement.Center,  //Divides the content equally, like SpaceEvenly, but in this case, the start and end composables inside the content are at the edges of the screen
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_music_note_white),
                tint = Theme.PlaybackScreenMiddleImageColor,
                contentDescription = "Songs list",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No songs yet\nImport new songs",
                color = Theme.PlaybackScreenContentColor,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {

        // Initialize LazyListState
        val listState = rememberLazyListState()

        // Find the index of the selected item
        val selectedIndex = playbackScreenState.mp3Items.indexOfFirst { it.isSelected }
        LaunchedEffect(selectedIndex) {
            if (selectedIndex >= 0) {
                // Scroll to make the selected item visible
                listState.animateScrollToItem(index = selectedIndex)
            }
        }

        LazyColumn(
            state = listState,
            modifier = modifier.then(
                Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            )
        ) {
            items(
                playbackScreenState.mp3Items
                , key = { it.uri }) {
                MP3MusicItem(mp3Item = it)
            }
        }
    }
}

//AppTodo(1): Add a selection background for the currently selected item before playing it
@Composable
@Preview
private fun MP3MusicItem(mp3Item: Mp3Item = Mp3Item(Uri.parse(""), "Music Title", true)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .conditional(
                mp3Item.isSelected, ifTrue = {
                    background(
                        colorResource(id = R.color.gray_nimbus_cloud),
                        RoundedCornerShape(8.dp)
                    )
                }
            )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_music_note_white),
            tint = Theme.PlaybackScreenMiddleImageColor,
            contentDescription = "Song",
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                .size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = mp3Item.title,
            color = Theme.PlaybackScreenContentColor,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
        )
    }
}
