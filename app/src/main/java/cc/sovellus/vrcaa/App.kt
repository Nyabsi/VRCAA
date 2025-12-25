/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import cc.sovellus.vrcaa.activity.CrashActivity
import cc.sovellus.vrcaa.base.BaseClient.AuthorizationType
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.crashAnalytics
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.minimalistMode
import cc.sovellus.vrcaa.extension.networkLogging
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.helper.NotificationHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import cc.sovellus.vrcaa.service.PipelineService

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

        Firebase.crashlytics.isCrashlyticsCollectionEnabled = preferences.crashAnalytics
        if (!preferences.crashAnalytics) {
            GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
        }

        NotificationHelper.createNotificationChannels()

        loadingText.value = applicationContext.getString(R.string.global_app_default_loading_text)

        if (preferences.authToken.isNotBlank() && preferences.twoFactorToken.isNotEmpty()) {
            api.setAuthorization(AuthorizationType.Cookie, "${preferences.authToken} ${preferences.twoFactorToken}")
            setIsValidSession(true)
            var intent = Intent(this, PipelineService::class.java)
            ContextCompat.startForegroundService(this, intent)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        private lateinit var preferences: SharedPreferences

        private var loadingText: MutableState<String> = mutableStateOf("")
        private var validSession: MutableState<Boolean> = mutableStateOf(false)

        fun getContext(): Context { return context }
        fun getPreferences(): SharedPreferences { return preferences }

        fun isNetworkLoggingEnabled(): Boolean { return preferences.networkLogging }
        fun isMinimalistModeEnabled(): Boolean { return preferences.minimalistMode }

        @Composable
        fun isAppInDarkTheme(): Boolean {
            if (preferences.currentThemeOption == 2)
                return isSystemInDarkTheme()
            return preferences.currentThemeOption != 0
        }

        fun getLoadingText(): MutableState<String> { return loadingText }
        fun setLoadingText(resourceId: Int) { loadingText.value = context.getString(resourceId) }

        fun getIsValidSession(): Boolean { return validSession.value }
        fun setIsValidSession(value: Boolean) { validSession.value = value }

        const val PREFERENCES_NAME = "vrcaa_prefs"
    }
}