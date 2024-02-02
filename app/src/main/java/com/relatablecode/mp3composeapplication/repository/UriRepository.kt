package com.relatablecode.mp3composeapplication.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.relatablecode.mp3composeapplication.datastore.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UriRepository(private val context: Context) {

    private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "mp3Preferences")
    private val dataStore: DataStore<Preferences> = context.preferencesDataStore

    suspend fun saveUri(uri: Uri) {
        // Convert Uri to String and save it using DataStore
        val uriString = uri.toString() // Convert Uri to String
        dataStore.edit { preferences ->
            // Retrieve current set of URIs or an empty set if none
            val currentUris = preferences[PreferencesKeys.SELECTED_URIS] ?: setOf()
            // Add the new URI string
            preferences[PreferencesKeys.SELECTED_URIS] = currentUris + uriString
        }
    }

    suspend fun deleteUri(uri: Uri) {
        // Remove Uri string from DataStore and release permissions if necessary
        val uriString = uri.toString() // Convert Uri to String for comparison
        dataStore.edit { preferences ->
            val currentUris = preferences[PreferencesKeys.SELECTED_URIS] ?: setOf()
            // Remove the URI string
            val updatedUris = currentUris - uriString
            preferences[PreferencesKeys.SELECTED_URIS] = updatedUris

            // Optionally, release the persistable URI permission if no longer needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val contentUri = Uri.parse(uriString)
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.releasePersistableUriPermission(contentUri, takeFlags)
            }
        }
    }

    val urisFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_URIS] ?: setOf()
        }

    // Add methods to take and release persistable URI permissions

}