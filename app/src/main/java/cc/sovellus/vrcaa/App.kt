package cc.sovellus.vrcaa

import android.app.Application
import android.content.Intent
import cc.sovellus.vrcaa.activity.crash.CrashActivity
import cc.sovellus.vrcaa.activity.crash.GlobalExceptionHandler
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.manager.NotificationManager
import cc.sovellus.vrcaa.service.PipelineService


class App : Application() {

    companion object {
        const val sharedPreferenceKey = "vrcaa_prefs"
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize crash handler
        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)

        // Initialize notification channels
        NotificationManager.createNotificationChannels(this)

        // if we have cookie, start service here.
        if (getSharedPreferences(sharedPreferenceKey, MODE_PRIVATE).authToken.isNotBlank()) {
            val intent = Intent(this, PipelineService::class.java)
            startService(intent)
        }
    }
}