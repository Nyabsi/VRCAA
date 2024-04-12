package cc.sovellus.vrcaa.ui.models.settings

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.helper.authToken
import cc.sovellus.vrcaa.helper.enableUpdates
import cc.sovellus.vrcaa.helper.invalidCookie
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val context: Context
) : ScreenModel {
    private val preferences = context.getSharedPreferences("vrcaa_prefs", 0)

    val enableUpdates = mutableStateOf(preferences.enableUpdates)

    fun doLogout() {
        screenModelScope.launch {
            val intent = Intent(context, PipelineService::class.java)
            context.stopService(intent)

            api?.logout()

            preferences.authToken = ""
            preferences.invalidCookie = true
        }
    }

    fun toggleUpdate(state: Boolean) {
        enableUpdates.value = state
        preferences.enableUpdates = state
    }
}