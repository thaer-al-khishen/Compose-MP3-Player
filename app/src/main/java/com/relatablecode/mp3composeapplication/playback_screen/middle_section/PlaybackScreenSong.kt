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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.relatablecode.mp3composeapplication.Mp3Item
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.Theme
import com.relatablecode.mp3composeapplication.playback_screen.state.PlaybackScreenState
import com.relatablecode.mp3composeapplication.timer.TimerViewModel
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
    exoPlayer: ExoPlayer,
    timerViewModel: TimerViewModel = viewModel() // Ensures ViewModel is scoped correctly
) {
    // Collecting state for current position and total duration
    val currentPosition by timerViewModel.timer.collectAsState()
    val duration by timerViewModel.duration.collectAsState()

    // Reset and initialize timer on song change
    LaunchedEffect(playbackScreenState.songBeingPlayed) {
        timerViewModel.setTimerDuration(playbackScreenState.songBeingPlayed?.duration ?: 0L)
        timerViewModel.stopTimer() // Consider if you want to reset the timer every time the song changes
    }

    // ExoPlayer state listener for play/pause actions
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) timerViewModel.startOrResumeTimer()
                else timerViewModel.pauseTimer()
            }
        }
        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener) }
    }

    // UI for displaying song details and progress
    SongDetailsUI(
        modifier = modifier,
        song = playbackScreenState.songBeingPlayed,
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
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_music_note_single), contentDescription = "Song", modifier = Modifier.size(50.dp))
        Spacer(modifier = Modifier.height(8.dp))

        song?.let {
            Text(text = it.title, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = currentPosition.coerceAtLeast(0L).toFloat(),
                onValueChange = { },
                valueRange = 0f..duration.toFloat(),
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            TimeDisplay(currentPosition = currentPosition, duration = duration)
        } ?: Text("No song playing yet")
    }
}

@Composable
fun TimeDisplay(currentPosition: Long, duration: Long) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = formatSeconds(currentPosition.div(1000).toFloat()), color = Theme.PlaybackScreenContentColor)
        Text(text = formatSeconds(duration.div(1000).toFloat()), color = Theme.PlaybackScreenContentColor)
    }
}

// Helper function to format seconds into a mm:ss string
@Composable
fun formatSeconds(seconds: Float): String {
    val minutes = (seconds.toInt() / 60).toString().padStart(2, '0')
    val remainingSeconds = (seconds.toInt() % 60).toString().padStart(2, '0')
    return "$minutes:$remainingSeconds"
}
