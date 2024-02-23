package com.relatablecode.mp3composeapplication.event_broadcaster

import com.relatablecode.mp3composeapplication.service.ServiceAction

//Used to broadcast the mp3 playback related events
object PlaybackEventBroadcaster {

    private val listOfListeners: MutableSet<EventListener> = HashSet()

    fun registerListener(eventListener: EventListener) {
        listOfListeners.add(eventListener)
    }

    fun unregisterListener(eventListener: EventListener) {
        listOfListeners.remove(eventListener)
    }

    fun emitAction(action: ServiceAction?) {
        listOfListeners.forEach {
            it.onEventReceived(action)
        }
    }

}
