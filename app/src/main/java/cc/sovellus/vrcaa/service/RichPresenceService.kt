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

package cc.sovellus.vrcaa.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.discord.GatewaySocket
import cc.sovellus.vrcaa.extension.discordToken
import cc.sovellus.vrcaa.extension.richPresenceWebhookUrl
import cc.sovellus.vrcaa.helper.NotificationHelper
import cc.sovellus.vrcaa.helper.StatusHelper
import cc.sovellus.vrcaa.manager.GatewayManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class RichPresenceService : Service() {

    private lateinit var preferences: SharedPreferences

    val gateway = GatewaySocket()

    val listener = object : GatewayManager.GatewayListener {
        override suspend fun onUpdateWorld(
            name: String,
            metadata: String,
            imageUrl: String,
            status: String,
            id: String
        ) {
            val newStatus = StatusHelper.getStatusFromString(status)
            gateway.sendPresence(name, metadata, imageUrl, id, newStatus)
        }

        override suspend fun onUpdateStatus(status: String) {
            val newStatus = StatusHelper.getStatusFromString(status)
            gateway.sendPresence(null, null, null, null, newStatus)
        }
    }

    override fun onCreate() {
        this.preferences = getSharedPreferences(App.PREFERENCES_NAME, 0)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val builder = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_DEFAULT_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(application.getString(R.string.app_name))
            .setContentText(application.getString(R.string.service_notification_rich_presence))
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setOngoing(false)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                builder.build(),
                FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            // Older versions do not require to specify the `foregroundServiceType`
            startForeground(NOTIFICATION_ID, builder.build())
        }

        GatewayManager.addListener(listener)
        gateway.setParams(preferences.discordToken, preferences.richPresenceWebhookUrl)
        gateway.connect()

        return START_STICKY
    }

    override fun onDestroy() {
        gateway.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? { return null }

    companion object {
        private const val NOTIFICATION_ID: Int = 42070
    }
}