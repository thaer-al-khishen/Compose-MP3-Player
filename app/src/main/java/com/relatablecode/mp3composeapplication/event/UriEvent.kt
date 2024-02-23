package com.relatablecode.mp3composeapplication.event

import android.net.Uri

sealed class UriEvent {
    data class SaveUriEvent(val uri: Uri): UriEvent()
    data class SaveUrisEvent(val uris: List<Uri>): UriEvent()
}
