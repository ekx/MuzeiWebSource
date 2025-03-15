package com.google.android.apps.muzei.websource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WebSourceDataStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("url_prefs")
        val URL_KEY = stringPreferencesKey("url_key")
        const val DEFAULT_URL = "https://unsplash.it/3840/2160?random"
    }

    val getUrl: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[URL_KEY] ?: DEFAULT_URL
    }

    suspend fun saveUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[URL_KEY] = url
        }
    }
}