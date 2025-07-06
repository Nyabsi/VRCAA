package cc.sovellus.vrcaa.ui.screen.presence

import android.content.Context
import android.content.Intent
import android.webkit.WebStorage
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.discordToken
import cc.sovellus.vrcaa.extension.richPresenceEnabled
import cc.sovellus.vrcaa.extension.richPresenceWebhookUrl
import cc.sovellus.vrcaa.service.RichPresenceService

class RichPresenceScreenModel : ScreenModel {

    private val context: Context = App.getContext()
    private val preferences = context.getSharedPreferences(App.PREFERENCES_NAME, 0)

    var token = mutableStateOf(preferences.discordToken)
    var enabled = mutableStateOf(preferences.richPresenceEnabled)
    var websocket = mutableStateOf(preferences.richPresenceWebhookUrl)

    fun updateTokenIfChanged() {
        if (token.value != preferences.discordToken)
            token.value = preferences.discordToken
    }

    fun revoke() {

        WebStorage.getInstance().deleteAllData()

        preferences.richPresenceEnabled = false
        preferences.discordToken = ""
        token.value = ""

        val intent = Intent(context, RichPresenceService::class.java)
        context.stopService(intent)
    }

    fun setWebSocket() {
        preferences.richPresenceWebhookUrl = websocket.value
    }

    fun toggle() {
        enabled.value = !enabled.value
        preferences.richPresenceEnabled = !preferences.richPresenceEnabled

        val intent = Intent(context, RichPresenceService::class.java)

        // we have no idea if it's actually running, nobody cares, it will start regardless.
        context.stopService(intent)
        ContextCompat.startForegroundService(context, intent)

        Toast.makeText(
            context,
            context.getString(R.string.discord_login_restarted_service_toast),
            Toast.LENGTH_LONG
        ).show()
    }
}