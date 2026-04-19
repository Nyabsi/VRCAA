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

package cc.sovellus.vrcaa.activity


import cc.sovellus.vrcaa.GlobalExceptionHandler
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.SlideTransition
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.base.BaseActivity
import cc.sovellus.vrcaa.base.BaseClient.AuthorizationType
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.onboardingCompleted
import cc.sovellus.vrcaa.extension.richPresenceEnabled
import cc.sovellus.vrcaa.extension.timeInBackground
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.helper.NotificationHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.service.PipelineService
import cc.sovellus.vrcaa.service.RichPresenceService
import cc.sovellus.vrcaa.ui.screen.login.LoginScreen
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen
import cc.sovellus.vrcaa.ui.screen.onboarding.OnboardingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val invalidSession = intent.extras?.getBoolean("INVALID_SESSION") == true
        val terminateSession = intent.extras?.getBoolean("TERMINATE_SESSION") == true
        val restartSession = intent.extras?.getBoolean("RESTART_SESSION") == true

        if (invalidSession) {

            preferences.authToken = ""

            var intent = Intent(this, PipelineService::class.java)
            stopService(intent)

            if (preferences.richPresenceEnabled) {
                intent = Intent(this, RichPresenceService::class.java)
                stopService(intent)
            }

            Toast.makeText(
                this,
                getString(R.string.api_session_has_expired_text),
                Toast.LENGTH_LONG
            ).show()

            App.setIsValidSession(false)
        }

        if (restartSession) {
            var intent = Intent(this, PipelineService::class.java)
            stopService(intent)
            ContextCompat.startForegroundService(this, intent)

            if (preferences.richPresenceEnabled) {
                intent = Intent(this, RichPresenceService::class.java)
                stopService(intent)
                ContextCompat.startForegroundService(this, intent)
            }
        }

        if (terminateSession) {
            var intent = Intent(this, PipelineService::class.java)
            stopService(intent)
            if (preferences.richPresenceEnabled) {
                intent = Intent(this, RichPresenceService::class.java)
                stopService(intent)
            }
            App.setIsValidSession(false)
        }

        if (savedInstanceState == null) {
            preferences.timeInBackground = 0

            GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
            NotificationHelper.createNotificationChannels()

            if (preferences.authToken.isNotBlank() && preferences.twoFactorToken.isNotEmpty()) {
                api.setAuthorization(AuthorizationType.Cookie, "${preferences.authToken} ${preferences.twoFactorToken}")
                App.setIsValidSession(true)
            }

            if (App.getIsValidSession()) {
                var intent = Intent(this, PipelineService::class.java)
                ContextCompat.startForegroundService(this, intent)
                if (preferences.richPresenceEnabled) {
                    intent = Intent(this, RichPresenceService::class.java)
                    ContextCompat.startForegroundService(this, intent)
                }
            }
        }
    }

    @Composable
    override fun Content(bundle: Bundle?) {
        Navigator(
            screen = if (!preferences.onboardingCompleted) {
                OnboardingScreen()
            } else if (App.getIsValidSession()) {
                NavigationScreen()
            } else {
                LoginScreen()
            },
            disposeBehavior = NavigatorDisposeBehavior(
                disposeNestedNavigators = false,
                disposeSteps = false
            ),
            onBackPressed = { true }
        ) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }

    override fun onPause() {
        super.onPause()
        preferences.timeInBackground = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        if (preferences.timeInBackground > 0) {
            val minutes = (System.currentTimeMillis() - preferences.timeInBackground) / (1000 * 60)
            if (minutes >= 15) {
                lifecycleScope.launch(Dispatchers.IO) {
                    CacheManager.buildCache()
                }
            }
        }
    }
}