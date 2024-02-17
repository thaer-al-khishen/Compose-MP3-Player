package com.relatablecode.mp3composeapplication.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.event_broadcaster.EventBroadcaster

class MusicPlaybackService: Service() {

    private var isPlayingMusic = false

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ServiceAction.PLAY_MUSIC.toString() -> playMusic()
            ServiceAction.RESUME_MUSIC.toString() -> resumeMusic()
            ServiceAction.PAUSE_MUSIC.toString() -> pauseMusic()
            ServiceAction.STOP_MUSIC.toString() -> stopMusic()
            ServiceAction.REWIND.toString() -> rewind()
            ServiceAction.FAST_FORWARD.toString() -> fastForward()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun playMusic() {
        // Create a notification
        val notification = createNotification()

        // Start foreground service
        startForeground(1, notification)

        // Perform the service task here (e.g., play music)
        // Start playing music
        EventBroadcaster.emitAction(ServiceAction.PLAY_MUSIC)
    }

    private fun resumeMusic() {
        EventBroadcaster.emitAction(ServiceAction.RESUME_MUSIC)
    }

    private fun rewind() {
        EventBroadcaster.emitAction(ServiceAction.REWIND)
    }

    private fun fastForward() {
        EventBroadcaster.emitAction(ServiceAction.FAST_FORWARD)
    }

    private fun pauseMusic() {
        //Pause the music
        EventBroadcaster.emitAction(ServiceAction.PAUSE_MUSIC)
    }

    private fun stopMusic() {
        //Stop the music
        EventBroadcaster.emitAction(ServiceAction.STOP_MUSIC)

        //Stop the foreground service
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(): Notification {
        // Intent to trigger play action
        val playIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ServiceAction.RESUME_MUSIC.toString()
        }
        val playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Intent to trigger pause action
        val pauseIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ServiceAction.PAUSE_MUSIC.toString()
        }
        val pausePendingIntent = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Intent to trigger rewind action
        val rewindIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ServiceAction.REWIND.toString()
        }
        val rewindPendingIntent = PendingIntent.getService(this, 2, rewindIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Intent to trigger fast forward action
        val fastForwardIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ServiceAction.FAST_FORWARD.toString()
        }
        val fastForwardPendingIntent = PendingIntent.getService(this, 3, fastForwardIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Use RemoteViews to set up custom layout
        val remoteViews = RemoteViews(packageName, R.layout.music_notification_layout).apply {
            setOnClickPendingIntent(R.id.ic_play, playPendingIntent)
            setOnClickPendingIntent(R.id.ic_pause, pausePendingIntent)
            setOnClickPendingIntent(R.id.ic_rewind, rewindPendingIntent)
            setOnClickPendingIntent(R.id.ic_forward, fastForwardPendingIntent)
        }

        // Build the notification
        return NotificationCompat.Builder(this, "MP3_SERVICE_CHANNEL")
            .setSmallIcon(R.drawable.ic_music_note_white)
            .setCustomContentView(remoteViews)
            .setSilent(true)
            .build()
    }

    override fun onDestroy() {
        EventBroadcaster.emitAction(ServiceAction.STOP_MUSIC)
        super.onDestroy()
    }

}
