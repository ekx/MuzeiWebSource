package com.google.android.apps.muzei.websource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WebSourceSettingsViewModel(private val dataStore: WebSourceDataStore) : ViewModel() {

    private val _urlInput = MutableStateFlow<String?>(null)
    val urlInput: StateFlow<String?> = _urlInput.asStateFlow()

    val storedUrl: StateFlow<String?> = dataStore.getUrl
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    suspend fun getStoredUrl(): String? {
        return dataStore.getUrl.firstOrNull()
    }

    fun onUrlChanged(newUrl: String) {
        _urlInput.value = newUrl
    }

    fun saveUrl(onCloseClicked: () -> Unit) {
        viewModelScope.launch {
            val currentUrlValue = _urlInput.value
            val storedUrlValue = storedUrl.value

            // Only save if there's a change and if currentUrlValue is not null, or if storedUrlValue is null and currentUrlValue is not null.
            if ((currentUrlValue != null && currentUrlValue != storedUrlValue) || (storedUrlValue == null && currentUrlValue != null)) {
                dataStore.saveUrl(currentUrlValue ?: "")
            }
            onCloseClicked()
        }
    }
}

class WebSourceSettingsViewModelFactory(private val dataStore: WebSourceDataStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WebSourceSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WebSourceSettingsViewModel(dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}