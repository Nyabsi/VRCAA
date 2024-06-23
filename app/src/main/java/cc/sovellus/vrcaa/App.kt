package cc.sovellus.vrcaa

import android.app.Application
import android.content.Intent
import cc.sovellus.vrcaa.activity.CrashActivity
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.manager.NotificationManager
import cc.sovellus.vrcaa.service.PipelineService

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize crash handler
        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)

        // Initialize notification channels
        NotificationManager.createNotificationChannels(this)
    }

    companion object {
        const val SHARED_PREFERENCES_KEY = "vrcaa_prefs"
    }
}