package com.google.android.apps.muzei.websource

import android.net.Uri
import androidx.core.net.toUri
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.MuzeiArtProvider
import com.google.android.apps.muzei.api.provider.ProviderContract
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.Date

class WebSourceArtProvider : MuzeiArtProvider() {
    override fun onLoadRequested(initial: Boolean) {
        val context = context ?: return

        val dataStore = WebSourceDataStore(context)
        val viewModel = WebSourceSettingsViewModel(dataStore)
        val providerClient = ProviderContract.getProviderClient(context, context.packageName)

        val currentUrl = runBlocking {
            viewModel.getStoredUrl()
        }

        val contentUri: Uri = currentUrl?.toUri() ?: Uri.EMPTY

        if (initial
            || lastAddedArtwork?.dateAdded?.before(oneSecondsAgo()) == true
            || lastAddedArtwork?.persistentUri != contentUri)
        {
            providerClient.setArtwork(
                Artwork(
                    title = context.getString(R.string.provider_name),
                    byline = contentUri.toString(),
                    persistentUri = contentUri,
                    webUri = contentUri
                )
            )
        }
    }

    override fun isArtworkValid(artwork: Artwork): Boolean {
        return artwork.dateAdded.after(oneDayAgo())
    }

    private fun oneSecondsAgo(): Date {
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.SECOND, -1)
        return cal.time
    }

    private fun oneDayAgo(): Date {
        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return cal.time
    }
}