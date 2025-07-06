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


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.SlideTransition
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.base.BaseActivity
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.richPresenceEnabled
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.service.PipelineService
import cc.sovellus.vrcaa.service.RichPresenceService
import cc.sovellus.vrcaa.ui.screen.login.LoginScreen
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen

class MainActivity : BaseActivity() {

    private var validSession = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: check is first time launch, redirect to "on-boarding" for permissions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }

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

        val token = preferences.authToken
        val twoFactorToken = preferences.twoFactorToken

        validSession = ((token.isNotBlank() && twoFactorToken.isNotEmpty()) && !invalidSession && !terminateSession)

        if (validSession) {
            var intent = Intent(this, PipelineService::class.java)
            ContextCompat.startForegroundService(this, intent)

            if (preferences.richPresenceEnabled) {
                intent = Intent(this, RichPresenceService::class.java)
                ContextCompat.startForegroundService(this, intent)
            }
        }
    }

    @Composable
    override fun Content(bundle: Bundle?) {

        Navigator(
            screen = if (validSession) {
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
}