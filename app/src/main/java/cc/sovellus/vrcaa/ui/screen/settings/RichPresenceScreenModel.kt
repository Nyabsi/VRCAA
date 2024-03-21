package cc.sovellus.vrcaa.ui.screen.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.discord.DiscordApi
import cc.sovellus.vrcaa.helper.discordToken
import cc.sovellus.vrcaa.helper.richPresenceEnabled
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

class RichPresenceScreenModel(
    private val context: Context
) : ScreenModel {
    private val preferences = context.getSharedPreferences("vrcaa_prefs", 0)

    var username = mutableStateOf("") // preferences.userCredentials.first.let { it ?: "" }
    var password = mutableStateOf("") // preferences.userCredentials.second.let { it ?: "" }
    var visibility = mutableStateOf(false)
    var token = mutableStateOf(preferences.discordToken)
    var enabled = mutableStateOf(preferences.richPresenceEnabled)

    fun login() {
        screenModelScope.launch {
            DiscordApi().login(username.value, password.value)?.let {
                preferences.discordToken = it.token
                token.value = it.token
            }
        }
    }

    fun revoke() {
        preferences.discordToken = ""
        token.value = ""
    }

    fun toggle() {
        enabled.value = !enabled.value
        preferences.richPresenceEnabled = !preferences.richPresenceEnabled

        val intent = Intent(context, PipelineService::class.java)

        context.stopService(intent)
        context.startForegroundService(intent)

        Toast.makeText(
            context,
            "restarted service to take effect.",
            Toast.LENGTH_LONG
        ).show()
    }
}