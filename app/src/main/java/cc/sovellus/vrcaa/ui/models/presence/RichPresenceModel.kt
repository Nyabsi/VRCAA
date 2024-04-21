package cc.sovellus.vrcaa.ui.models.presence

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.discord.DiscordApi
import cc.sovellus.vrcaa.api.discord.models.DiscordLogin
import cc.sovellus.vrcaa.api.discord.models.DiscordTicket
import cc.sovellus.vrcaa.extension.discordToken
import cc.sovellus.vrcaa.extension.richPresenceEnabled
import cc.sovellus.vrcaa.extension.richPresenceWebhookUrl
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

class RichPresenceModel(
    private val context: Context
) : ScreenModel {

    private val preferences = context.getSharedPreferences("vrcaa_prefs", 0)

    private var ticket = mutableStateOf("")

    var token = mutableStateOf(preferences.discordToken)
    var enabled = mutableStateOf(preferences.richPresenceEnabled)
    var websocket = mutableStateOf(preferences.richPresenceWebhookUrl)

    var visibility = mutableStateOf(false)

    var username = mutableStateOf("")
    var password = mutableStateOf("")

    var mfa = mutableStateOf(false)
    var code = mutableStateOf("")

    fun login() {
        screenModelScope.launch {
            when (val result = DiscordApi().login(username.value, password.value)) {
                is DiscordLogin -> {
                    preferences.discordToken = result.token
                    token.value = result.token
                }

                is DiscordTicket -> {
                    ticket.value = result.ticket
                    mfa.value = true
                }

                else -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.discord_login_toast_wrong_credentials),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun mfa() {
        screenModelScope.launch {
            when (val result = DiscordApi().mfa(ticket.value, code.value)) {
                is DiscordLogin -> {
                    preferences.discordToken = result.token
                    token.value = result.token
                    mfa.value = false
                }

                else -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.discord_login_toast_wrong_code),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun revoke() {
        preferences.discordToken = ""
        token.value = ""
    }

    fun setWebSocket() {
        preferences.richPresenceWebhookUrl = websocket.value
    }

    fun toggle() {
        enabled.value = !enabled.value
        preferences.richPresenceEnabled = !preferences.richPresenceEnabled

        val intent = Intent(context, PipelineService::class.java)

        context.stopService(intent)
        context.startService(intent)

        Toast.makeText(
            context,
            context.getString(R.string.discord_login_restarted_service_toast),
            Toast.LENGTH_LONG
        ).show()
    }
}