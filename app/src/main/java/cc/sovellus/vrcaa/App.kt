package cc.sovellus.vrcaa

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cc.sovellus.vrcaa.activity.CrashActivity
import cc.sovellus.vrcaa.extension.developerMode
import cc.sovellus.vrcaa.manager.NotificationManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize crash handler
        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)

        // Initialize notification channels
        NotificationManager.createNotificationChannels(this)

        developerModeEnabled = this.getSharedPreferences("vrcaa_prefs", 0).developerMode
    }

    companion object {
        private var developerModeEnabled: Boolean = false

        fun isDeveloperModeEnabled(): Boolean {
            return developerModeEnabled
        }
    }
}