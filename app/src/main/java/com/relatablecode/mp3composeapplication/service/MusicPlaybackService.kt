package com.relatablecode.mp3composeapplication.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.relatablecode.mp3composeapplication.R
import com.relatablecode.mp3composeapplication.event_broadcaster.EventBroadcaster

class MusicPlaybackService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ServiceAction.PLAY_MUSIC.toString() -> playMusic()
            ServiceAction.RESUME_MUSIC.toString() -> resumeMusic()
            ServiceAction.PAUSE_MUSIC.toString() -> pauseMusic()
            ServiceAction.STOP_MUSIC.toString() -> stopMusic()
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
        val builder = NotificationCompat.Builder(this, "MP3_SERVICE_CHANNEL")
            .setContentTitle("Music Player")
            .setContentText("Playing your favorite music!")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setSilent(true)

        return builder.build()
    }

    override fun onDestroy() {
        EventBroadcaster.emitAction(ServiceAction.STOP_MUSIC)
        super.onDestroy()
    }

}
