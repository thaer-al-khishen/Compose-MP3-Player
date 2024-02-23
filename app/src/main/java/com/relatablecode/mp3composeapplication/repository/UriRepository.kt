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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriRepository @Inject constructor(@ApplicationContext val appContext: Context) {

    private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "mp3Preferences")
    private val dataStore: DataStore<Preferences> = appContext.preferencesDataStore

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

    //Created to save multiple uris when the user wants to import several mp3 files at once
    suspend fun saveUris(uris: List<Uri>) {
        val uniqueUris = uris.map(Uri::toString).toSet() // Convert to strings and eliminate any duplicates within the batch
        dataStore.edit { preferences ->
            val currentUris = preferences[PreferencesKeys.SELECTED_URIS] ?: setOf()
            preferences[PreferencesKeys.SELECTED_URIS] = currentUris + uniqueUris // Adds the batch, set ensures uniqueness
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
                appContext.contentResolver.releasePersistableUriPermission(contentUri, takeFlags)
            }
        }
    }

    val urisFlow: Flow<Set<String>> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_URIS] ?: setOf()
        }

}
