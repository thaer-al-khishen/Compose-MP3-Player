package com.relatablecode.mp3composeapplication

import android.net.Uri

data class Mp3Item(
    val uri: Uri,
    val title: String,
    var isSelected: Boolean = false,
    var duration: Long = 0L // Duration in milliseconds
)
