package com.relatablecode.mp3composeapplication.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.event_broadcaster.playback.PlaybackEventBroadcaster
import com.relatablecode.mp3composeapplication.event_broadcaster.song.SongBroadcaster
import com.relatablecode.mp3composeapplication.event_broadcaster.song.SongListener

class MusicPlaybackService : Service(), SongListener {

    private var isPlayingMusic = false
    private var _songTitle = "Song"

    override fun onCreate() {
        super.onCreate()
        SongBroadcaster.registerListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ServiceAction.PLAY_MUSIC.toString() -> {
                isPlayingMusic = true
                playMusic()
            }

            ServiceAction.RESUME_MUSIC.toString() -> {
                isPlayingMusic = true
                resumeMusic()
            }

            ServiceAction.PAUSE_MUSIC.toString() -> {
                isPlayingMusic = false
                pauseMusic()
            }

            ServiceAction.STOP_MUSIC.toString() -> stopMusic()
            ServiceAction.REWIND.toString() -> rewind()
            ServiceAction.FAST_FORWARD.toString() -> fastForward()
        }
        updateNotification()
        return START_NOT_STICKY
    }

    private fun playMusic() {
        // Handle music playing logic
        PlaybackEventBroadcaster.emitAction(ServiceAction.PLAY_MUSIC)
    }

    private fun resumeMusic() {
        // Handle music resuming logic
        PlaybackEventBroadcaster.emitAction(ServiceAction.RESUME_MUSIC)
    }

    private fun pauseMusic() {
        // Handle music pausing logic
        PlaybackEventBroadcaster.emitAction(ServiceAction.PAUSE_MUSIC)
    }

    private fun stopMusic() {
        // Handle music stopping logic
        isPlayingMusic = false
        PlaybackEventBroadcaster.emitAction(ServiceAction.STOP_MUSIC)
        stopForeground(true)
        stopSelf()
    }

    private fun rewind() {
        // Handle rewind logic
        PlaybackEventBroadcaster.emitAction(ServiceAction.REWIND)
    }

    private fun fastForward() {
        // Handle fast forward logic
        PlaybackEventBroadcaster.emitAction(ServiceAction.FAST_FORWARD)
    }

    private fun updateNotification() {

        // Intent to trigger rewind action
        val rewindIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ServiceAction.REWIND.toString()
        }
        val rewindPendingIntent =
            PendingIntent.getService(this, 2, rewindIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Intent to trigger fast forward action
        val fastForwardIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ServiceAction.FAST_FORWARD.toString()
        }
        val fastForwardPendingIntent =
            PendingIntent.getService(this, 3, fastForwardIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playPauseIcon =
            if (isPlayingMusic) R.drawable.ic_pause_menu else R.drawable.ic_play_menu
        val playPauseIntentAction =
            if (isPlayingMusic) ServiceAction.PAUSE_MUSIC.toString() else ServiceAction.RESUME_MUSIC.toString()

        val playPauseIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = playPauseIntentAction
        }
        val playPausePendingIntent =
            PendingIntent.getService(this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val remoteViews = RemoteViews(packageName, R.layout.music_notification_layout).apply {
            setOnClickPendingIntent(R.id.ic_rewind, rewindPendingIntent)
            setOnClickPendingIntent(R.id.ic_forward, fastForwardPendingIntent)
            setOnClickPendingIntent(R.id.ic_play_pause, playPausePendingIntent)
            setTextViewText(R.id.tv_song_title, _songTitle)
            setImageViewResource(R.id.ic_play_pause, playPauseIcon)
            // Update other buttons/actions as needed
        }

        val notification = NotificationCompat.Builder(this, "MP3_SERVICE_CHANNEL")
            .setSmallIcon(R.drawable.ic_music_note_white)
            .setCustomContentView(remoteViews)
            .setSilent(true)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        PlaybackEventBroadcaster.emitAction(ServiceAction.STOP_MUSIC)
//        SongBroadcaster.unregisterListener(this)
        super.onDestroy()
    }

    override fun onSongReceived(songTitle: String?) {
        _songTitle = songTitle ?: "Song"
        updateNotification()
    }

}
