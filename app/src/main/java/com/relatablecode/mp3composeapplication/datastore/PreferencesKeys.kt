package com.relatablecode.mp3composeapplication.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

//To be used for data store logic inside repositories
object PreferencesKeys {
    val SELECTED_URIS = stringSetPreferencesKey("selected_uris")
    val CURRENT_THEME_INDEX= intPreferencesKey("current_theme_index")
}
