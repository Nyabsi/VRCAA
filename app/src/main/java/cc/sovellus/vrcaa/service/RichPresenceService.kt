package cc.sovellus.vrcaa.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
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

class RichPresenceService : Service(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main + SupervisorJob()

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
        GatewayManager.setListener(listener)
        val preferences = getSharedPreferences("vrcaa_prefs", 0)
        gateway.setParams(preferences.discordToken, preferences.richPresenceWebhookUrl)
        gateway.connect()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val builder = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_DEFAULT_ID)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(application.getString(R.string.app_name))
            .setContentText("Started the epic background service for the great rich presence") // application.getString(R.string.service_notification)
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

        return START_STICKY_COMPATIBILITY
    }

    override fun onDestroy() {
        gateway.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? { return null }

    companion object {
        private const val NOTIFICATION_ID: Int = 42070
    }
}