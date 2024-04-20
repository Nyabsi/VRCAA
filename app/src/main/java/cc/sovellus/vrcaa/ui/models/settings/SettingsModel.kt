package cc.sovellus.vrcaa.ui.models.settings

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.activity.main.MainActivity
import cc.sovellus.vrcaa.helper.cookies
import cc.sovellus.vrcaa.helper.enableUpdates
import cc.sovellus.vrcaa.helper.invalidCookie
import cc.sovellus.vrcaa.helper.twoFactorAuth
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

class SettingsModel(
    private val context: Context
) : ScreenModel {
    private val preferences = context.getSharedPreferences("vrcaa_prefs", 0)

    val enableUpdates = mutableStateOf(preferences.enableUpdates)

    fun doLogout() {
        screenModelScope.launch {
            var intent: Intent?

            intent = Intent(context, PipelineService::class.java)
            context.stopService(intent)

            api?.logout()

            preferences.twoFactorAuth = ""
            preferences.cookies = ""
            preferences.invalidCookie = true

            intent = Intent(context, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun toggleUpdate(state: Boolean) {
        enableUpdates.value = state
        preferences.enableUpdates = state
    }
}