package com.relatablecode.mp3composeapplication.repository

import android.content.Context
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
class ThemeRepository @Inject constructor(@ApplicationContext val appContext: Context) {

    private val dataStore: DataStore<Preferences> = appContext.preferencesDataStore

    suspend fun saveCurrentThemeIndex(index: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_THEME_INDEX] = index
        }
    }

    val currentThemeIndex: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CURRENT_THEME_INDEX] ?: 0 // Default to 0 or whatever your default theme index is
        }

    companion object {
        private val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "themePreferences")
    }

}
