package com.relatablecode.mp3composeapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.event_broadcaster.EventBroadcaster
import com.relatablecode.mp3composeapplication.event_broadcaster.EventListener
import com.relatablecode.mp3composeapplication.mp3_player_device.MP3PlayerDevice
import com.relatablecode.mp3composeapplication.service.MusicPlaybackService
import com.relatablecode.mp3composeapplication.service.ServiceAction
import com.relatablecode.mp3composeapplication.timer.TimerManager
import com.relatablecode.mp3composeapplication.utils.UriUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity(), EventListener {

    // For a single audio file selection
    // Register the contract for picking a single document, allowing persistable permission.
    private val pickAudioFile =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->

            uri?.let {
                try {
                    // Take persistable URI permission.
                    contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    // Proceed to handle the URI
                    saveUri(it)
                    navigateToMusicList()
                } catch (e: SecurityException) {
                    // Handle the exception, possibly by informing the user
                    Log.e("MainActivity", "Failed to take persistable URI permission", e)
                }
            }
        }

    // For multiple audio file selections
    private val pickMultipleAudioFiles =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri> ->
            // Handle the returned URIs
            uris.forEach { uri ->
                try {
                    contentResolver.takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    Log.e("MainActivity", "Failed to take persistable URI permission", e)
                }
            }
            saveUris(uris) // Saves all at once, ensuring uniqueness
            navigateToMusicList()
        }

    private val viewModel: MP3PlayerViewModel by viewModels()

    private lateinit var exoPlayer: ExoPlayer

    private var songUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureStatusBarFullScreenTransparency()
        setupObservers()

        // Initialize ExoPlayer
        initializePlayer()
        EventBroadcaster.registerListener(this)
        observeExoPlayerStateChanges()

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val playbackScreenState = viewModel.playbackScreenState.collectAsState()
                MP3PlayerDevice(
                    exoPlayer = exoPlayer, playbackScreenState.value, viewModel::onEvent
                )
            }
        }
    }

    private fun configureStatusBarFullScreenTransparency() {
        // Make status and navigation bars transparent and handle insets
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.apply {
            hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            // For light status bar icons (dark icons) on light backgrounds
            isAppearanceLightStatusBars = true
            // Additionally for light navigation bar icons if necessary
            // isAppearanceLightNavigationBars = true
        }
    }

    private fun setupObservers() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.Main.immediate) {
                    viewModel.mp3PlayerEvent.collect {
                        when (it) {
                            is MP3PlayerEvent.AccessMediaSingleFile -> {
                                showSingleFilePicker()
                            }

                            is MP3PlayerEvent.AccessMediaMultipleFiles -> {
                                showMultipleFilePicker()
                            }

                            is MP3PlayerEvent.PlaySong -> {
                                playMusic(it.uri)
                            }

                            is MP3PlayerEvent.ResumeSong -> {
                                resumeMusic()
                            }

                            is MP3PlayerEvent.PauseSong -> {
                                pauseMusic()
                            }

                            is MP3PlayerEvent.ShowDeleteSongUI -> {
                                AlertDialog.Builder(this@MainActivity).apply {
                                    setTitle("Delete Song")
                                    setMessage("Are you sure you want to delete this song?")
                                    setPositiveButton("Yes") { dialog, _ ->
                                        deleteSong()
                                        dialog.dismiss()
                                    }
                                    setNegativeButton("No") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    show()
                                }
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                retrieveUris().collect { uriSet ->
                    // Initialize an empty list to hold Mp3Item objects
                    val mp3Items = mutableListOf<Mp3Item>()

                    // Iterate through each URI string
                    uriSet.forEach { uri ->
                        val title = getFileName(this@MainActivity, uri) ?: "Unknown Title"
                        val duration = UriUtils.getSongDuration(this@MainActivity, uri)
                        // Create an Mp3Item and add it to the list
                        mp3Items.add(Mp3Item(uri = uri, title = title, duration = duration))
                    }

                    // Update your PlaybackScreenState with the new list of Mp3Items
                    viewModel.updateMp3Items(mp3Items)
                }
            }
        }

    }

    private fun showSingleFilePicker() {
        pickAudioFile.launch(arrayOf("audio/*")) // MIME type for audio files
    }

    private fun showMultipleFilePicker() {
        pickMultipleAudioFiles.launch(arrayOf("audio/*")) // MIME type for audio files
    }

    private fun playMusic(uri: Uri) {
        songUri = uri
        val serviceIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ServiceAction.PLAY_MUSIC.toString()
        }
        startService(serviceIntent)
    }

    private fun pauseMusic() {
        val serviceIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ServiceAction.PAUSE_MUSIC.toString()
        }
        startService(serviceIntent)
    }

    private fun resumeMusic() {
        val serviceIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ServiceAction.RESUME_MUSIC.toString()
        }
        startService(serviceIntent)
    }

    private fun stopMusic() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        val serviceIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ServiceAction.STOP_MUSIC.toString()
        }
        startService(serviceIntent)
    }

    private fun initializePlayer() {
        // Initialization logic here if needed
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            // Add media items, prepare and other initial setup if necessary
        }
    }

    private fun preparePlayerWithUri(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            // moveToFirst() returns false if the cursor has 0 rows. Very useful for
            // "if not found, return" logic.
            if (cursor.moveToFirst()) {
                // Note it's called "DISPLAY_NAME", not "TITLE"
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }

    private fun saveUri(uri: Uri) {
        viewModel.saveUri(uri)
    }

    //Created to save multiple uris when the user wants to import several mp3 files at once
    private fun saveUris(uris: List<Uri>) {
        viewModel.saveUris(uris)
    }

    private fun retrieveUris(): StateFlow<Set<Uri>> {
        return viewModel.uris
    }

    private fun deleteUri(uri: Uri) {
        viewModel.deleteUri(uri)
    }

    private fun deleteSong() {
        viewModel.deleteSong()
    }

    private fun navigateToMusicList() {
        viewModel.navigateToMusicList()
    }

    private fun observeExoPlayerStateChanges() {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) TimerManager.startOrResumeTimer()
                else TimerManager.pauseTimer()
            }
        }
        exoPlayer.addListener(listener)
    }

    override fun onEventReceived(action: ServiceAction?) {
        when (action) {
            ServiceAction.PLAY_MUSIC -> {
                songUri?.let {
                    TimerManager.stopTimer()
                    TimerManager.setTimerDuration(viewModel.playbackScreenState.value.songBeingPlayed?.duration ?: 0L)
                    TimerManager.startOrResumeTimer() // Consider if you want to reset the timer every time the song changes
                    val mediaItem = MediaItem.fromUri(it)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.play()
                }
            }

            ServiceAction.RESUME_MUSIC -> {
                if (!exoPlayer.isPlaying) {
                    TimerManager.startOrResumeTimer() // Consider if you want to reset the timer every time the song changes
                    exoPlayer.play()
                }
            }

            ServiceAction.PAUSE_MUSIC -> {
                TimerManager.pauseTimer()
                exoPlayer.pause()
            }

            ServiceAction.STOP_MUSIC -> {
                TimerManager.stopTimer()
                stopMusic()
            }

            else -> {}
        }
    }

}
