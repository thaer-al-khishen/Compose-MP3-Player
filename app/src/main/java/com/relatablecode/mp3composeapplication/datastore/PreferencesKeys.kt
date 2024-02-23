package com.relatablecode.mp3composeapplication.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferencesKeys {
    val SELECTED_URIS = stringSetPreferencesKey("selected_uris")
    val CURRENT_THEME_INDEX= intPreferencesKey("current_theme_index")
}
