package com.relatablecode.mp3composeapplication

import android.net.Uri

data class Mp3Item(
    val uri: Uri,
    val title: String,
    val isSelected: Boolean = false
)
