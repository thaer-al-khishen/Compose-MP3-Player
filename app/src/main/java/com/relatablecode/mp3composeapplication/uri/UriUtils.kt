package com.relatablecode.mp3composeapplication.uri

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

object UriUtils {

    fun getSongDuration(context: Context, uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            return durationString?.toLong() ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        } finally {
            retriever.release()
        }
    }

}
