package cc.sovellus.vrcaa

import android.app.Application
import android.util.Log

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.d("VRCAA", "Init notifications, etc...")
    }
}