package com.google.android.apps.muzei.websource

import android.app.Service
import android.content.Intent
import android.os.IBinder

class WebSourceArtSource : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}