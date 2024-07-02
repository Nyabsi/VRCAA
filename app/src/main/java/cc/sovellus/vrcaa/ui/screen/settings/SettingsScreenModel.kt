package cc.sovellus.vrcaa.ui.screen.settings

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.activity.LoginActivity
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.updatesEnabled
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val context: Context
) : ScreenModel {
    private val preferences = context.getSharedPreferences("vrcaa_prefs", 0)

    val enableUpdates = mutableStateOf(preferences.updatesEnabled)

    fun doLogout() {
        screenModelScope.launch {
            var intent: Intent?

            intent = Intent(context, PipelineService::class.java)
            context.stopService(intent)

            api.logout()

            preferences.authToken = ""

            intent = Intent(context, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun toggleUpdate(state: Boolean) {
        enableUpdates.value = state
        preferences.updatesEnabled = state
    }
}