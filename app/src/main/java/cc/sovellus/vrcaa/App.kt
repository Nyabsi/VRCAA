package cc.sovellus.vrcaa

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import cc.sovellus.vrcaa.activity.crash.CrashActivity
import cc.sovellus.vrcaa.activity.crash.GlobalExceptionHandler
import cc.sovellus.vrcaa.manager.NotificationManager


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize crash handler
        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)

        // Initialize notification channels
        NotificationManager.createNotificationChannels(this)
    }
}