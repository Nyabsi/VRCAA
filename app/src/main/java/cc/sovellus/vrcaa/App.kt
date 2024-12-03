package cc.sovellus.vrcaa

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cc.sovellus.vrcaa.activity.CrashActivity
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.networkLogging
import cc.sovellus.vrcaa.extension.minimalistMode
import cc.sovellus.vrcaa.helper.NotificationHelper

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        context = this
        preferences = getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)

        networkLogging.value = preferences.networkLogging
        minimalistModeEnabled.value = preferences.minimalistMode

        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
        NotificationHelper.createNotificationChannels()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        private lateinit var preferences: SharedPreferences

        private var networkLogging: MutableState<Boolean> = mutableStateOf(false)
        private var minimalistModeEnabled: MutableState<Boolean> = mutableStateOf(false)

        private var loadingText: MutableState<String> = mutableStateOf("")

        fun getContext(): Context { return context }

        fun isNetworkLoggingEnabled(): Boolean { return networkLogging.value }
        fun isMinimalistModeEnabled(): Boolean { return minimalistModeEnabled.value }

        @Composable
        fun isAppInDarkTheme(): Boolean { return isSystemInDarkTheme() && preferences.currentThemeOption != 0 }

        fun getLoadingText(): MutableState<String> { return loadingText }
        fun setLoadingText(resourceId: Int) { loadingText.value = context.getString(resourceId) }
    }
}