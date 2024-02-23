package com.relatablecode.mp3composeapplication.event_broadcaster.playback

import com.relatablecode.mp3composeapplication.service.ServiceAction

interface PlaybackEventListener {
    fun onEventReceived(action: ServiceAction?)
}
