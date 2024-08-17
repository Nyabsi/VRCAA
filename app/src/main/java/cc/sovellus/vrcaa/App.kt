package cc.sovellus.vrcaa

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cc.sovellus.vrcaa.activity.CrashActivity
import cc.sovellus.vrcaa.extension.developerMode
import cc.sovellus.vrcaa.helper.NotificationHelper

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
        NotificationHelper.createNotificationChannels(this)

        mContext = this
        developerModeEnabled = this.getSharedPreferences("vrcaa_prefs", 0).developerMode
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var mContext: Context

        private var developerModeEnabled: Boolean = false
        private var loadingText: MutableState<String> = mutableStateOf("")

        fun isDeveloperModeEnabled(): Boolean { return developerModeEnabled }
        fun getLoadingText(): MutableState<String> { return loadingText }
        fun setLoadingText(resourceId: Int) { loadingText.value = mContext.getString(resourceId) }
    }
}