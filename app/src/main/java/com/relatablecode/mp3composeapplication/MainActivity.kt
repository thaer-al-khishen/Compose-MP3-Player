package com.relatablecode.mp3composeapplication

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.relatablecode.mp3composeapplication.datastore.PreferencesKeys
import com.relatablecode.mp3composeapplication.event.MP3PlayerEvent
import com.relatablecode.mp3composeapplication.mp3_player_device.MP3PlayerDevice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // For a single audio file selection
    // Register the contract for picking a single document, allowing persistable permission.
    private val pickAudioFile = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->

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
    private val pickMultipleAudioFiles = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri> ->
        // Handle the returned URIs
        uris.forEach { uri ->
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: SecurityException) {
                Log.e("MainActivity", "Failed to take persistable URI permission", e)
            }
        }
        saveUris(uris) // Saves all at once, ensuring uniqueness
        navigateToMusicList()
    }

    private val viewModel: MP3PlayerViewModel by viewModels()

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureStatusBarFullScreenTransparency()
        setupObservers()

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val playbackScreenState = viewModel.playbackScreenState.collectAsState()
                MP3PlayerDevice(playbackScreenState.value, viewModel::onEvent)
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
                        when(it) {
                            is MP3PlayerEvent.AccessMediaSingleFile -> {
                                showSingleFilePicker()
                            }
                            is MP3PlayerEvent.AccessMediaMultipleFiles -> {
                                showMultipleFilePicker()
                            }
                            is MP3PlayerEvent.PlaySong -> {
                                playMusic(it.uri)
                            }
                            is MP3PlayerEvent.PauseSong -> {
                                stopMusic()
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
                        // Create an Mp3Item and add it to the list
                        mp3Items.add(Mp3Item(uri, title))
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
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(this@MainActivity, uri)
            prepare() // Consider using prepareAsync() for streaming over the network
            start()
        }
        mediaPlayer?.start()
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
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

}
