package com.relatablecode.mp3composeapplication.event_broadcaster.playback

import com.relatablecode.mp3composeapplication.service.ServiceAction

//Used to broadcast the mp3 playback related events from the MusicPlaybackService
object PlaybackEventBroadcaster {

    private val listOfListeners: MutableSet<PlaybackEventListener> = HashSet()

    fun registerListener(playbackEventListener: PlaybackEventListener) {
        listOfListeners.add(playbackEventListener)
    }

    fun unregisterListener(playbackEventListener: PlaybackEventListener) {
        listOfListeners.remove(playbackEventListener)
    }

    fun emitAction(action: ServiceAction?) {
        listOfListeners.forEach {
            it.onEventReceived(action)
        }
    }

}
