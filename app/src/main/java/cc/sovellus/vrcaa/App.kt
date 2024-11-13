package cc.sovellus.vrcaa

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cc.sovellus.vrcaa.activity.CrashActivity
import cc.sovellus.vrcaa.extension.crashAnalytics
import cc.sovellus.vrcaa.extension.developerMode
import cc.sovellus.vrcaa.helper.NotificationHelper
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val preferences = this.getSharedPreferences("vrcaa_prefs", 0)

        Firebase.crashlytics.isCrashlyticsCollectionEnabled = preferences.crashAnalytics
        if (!preferences.crashAnalytics)
            GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
        NotificationHelper.createNotificationChannels(this)

        context = this
        developerModeEnabled.value = preferences.developerMode
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        private var developerModeEnabled: MutableState<Boolean> = mutableStateOf(false)
        private var loadingText: MutableState<String> = mutableStateOf("")

        fun getContext(): Context { return context }

        fun isDeveloperModeEnabled(): Boolean { return developerModeEnabled.value }

        fun getLoadingText(): MutableState<String> { return loadingText }
        fun setLoadingText(resourceId: Int) { loadingText.value = context.getString(resourceId) }
    }
}