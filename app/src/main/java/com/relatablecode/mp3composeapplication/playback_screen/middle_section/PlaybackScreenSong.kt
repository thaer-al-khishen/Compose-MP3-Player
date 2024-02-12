package com.relatablecode.mp3composeapplication.playback_screen.middle_section

import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.Theme
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun PlaybackScreenSong(
    modifier: Modifier = Modifier,
    playbackScreenState: PlaybackScreenState,
    exoPlayer: ExoPlayer
) {

    val currentPosition = remember { Animatable(0f) }
    val totalTime = remember { mutableStateOf(playbackScreenState.songBeingPlayed?.duration?.div(1000f) ?: 0f) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                coroutineScope.launch { currentPosition.animateTo(exoPlayer.currentPosition / 1000f) }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    coroutineScope.launch {
                        while (isActive) {
                            currentPosition.animateTo(exoPlayer.currentPosition / 1000f)
                            delay(500)
                        }
                    }
                }
            }

        }

        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Icon, Spacer, and Song title...
        Icon(
            painter = painterResource(id = R.drawable.ic_music_note_single),
            tint = Theme.PlaybackScreenMiddleImageColor,
            contentDescription = "Song",
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        playbackScreenState.songBeingPlayed?.let {

            Text(text = it.title, color = Theme.PlaybackScreenContentColor, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // Seekbar for the song (read-only)
            Slider(
                value = currentPosition.value.coerceAtLeast(0f),
                onValueChange = { /* Intentionally left blank to disable manual seek */ },
                valueRange = 0f..totalTime.value,
                enabled = false, // Disable touch interaction
                modifier = Modifier.fillMaxWidth()
            )

            // Start and end time texts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatSeconds(currentPosition.value),
                    color = Theme.PlaybackScreenContentColor
                )
                Text(
                    text = formatSeconds(totalTime.value),
                    color = Theme.PlaybackScreenContentColor
                )
            }

        } ?: run {
            Text(
                text = "No song playing yet",
                color = Theme.PlaybackScreenContentColor,
                fontSize = 16.sp
            )
        }

    }

}

// Helper function to format seconds into a mm:ss string
@Composable
fun formatSeconds(seconds: Float): String {
    val minutes = (seconds.toInt() / 60).toString().padStart(2, '0')
    val remainingSeconds = (seconds.toInt() % 60).toString().padStart(2, '0')
    return "$minutes:$remainingSeconds"
}
