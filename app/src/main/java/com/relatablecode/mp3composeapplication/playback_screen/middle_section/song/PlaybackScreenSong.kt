package com.relatablecode.mp3composeapplication.playback_screen.middle_section.song

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.relatablecode.mp3composeapplication.Mp3Item
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.theme.LocalAppTheme
import com.relatablecode.mp3composeapplication.timer.TimerManager

@Composable
fun PlaybackScreenSong(
    modifier: Modifier = Modifier,
    songState: SongState,   //You only need the song being played
) {
    // Collecting state for current position and total duration
    val currentPosition by TimerManager.timer.collectAsState()
    val duration by TimerManager.duration.collectAsState()

    // UI for displaying song details and progress
    SongDetailsUI(
        modifier = modifier,
        song = songState.songBeingPlayed,
        currentPosition = currentPosition,
        duration = duration
    )
}

@Composable
fun SongDetailsUI(
    modifier: Modifier,
    song: Mp3Item?,
    currentPosition: Long,
    duration: Long
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_music_note_single),
            contentDescription = "Song",
            modifier = Modifier.size(50.dp),
            tint = LocalAppTheme.current.playbackScreenMiddleImageColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        song?.let {
            Text(text = it.title, fontSize = 16.sp, color = LocalAppTheme.current.playbackScreenContentColor)
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = currentPosition.coerceAtLeast(0L).toFloat(),
                onValueChange = { },
                valueRange = 0f..duration.toFloat(),
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            TimeDisplay(currentPosition = currentPosition, duration = duration)
        } ?: Text("No song playing yet", color = LocalAppTheme.current.playbackScreenContentColor)
    }
}

@Composable
fun TimeDisplay(currentPosition: Long, duration: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formatSeconds(currentPosition.div(1000).toFloat()),
            color = LocalAppTheme.current.playbackScreenContentColor
        )
        Text(
            text = formatSeconds(duration.div(1000).toFloat()),
            color = LocalAppTheme.current.playbackScreenContentColor
        )
    }
}

// Helper function to format seconds into a mm:ss string
@Composable
fun formatSeconds(seconds: Float): String {
    val minutes = (seconds.toInt() / 60).toString().padStart(2, '0')
    val remainingSeconds = (seconds.toInt() % 60).toString().padStart(2, '0')
    return "$minutes:$remainingSeconds"
}
