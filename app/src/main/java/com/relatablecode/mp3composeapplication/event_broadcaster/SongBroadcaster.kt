package com.relatablecode.mp3composeapplication.event_broadcaster

object SongBroadcaster {

    private val listOfListeners: MutableSet<SongListener> = HashSet()

    fun registerListener(eventListener: SongListener) {
        listOfListeners.add(eventListener)
    }

    fun unregisterListener(eventListener: SongListener) {
        listOfListeners.remove(eventListener)
    }

    fun emitAction(songTitle: String?) {
        listOfListeners.forEach {
            it.onSongReceived(songTitle)
        }
    }

}
