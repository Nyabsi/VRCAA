package cc.sovellus.vrcaa.ui.screen.settings

import android.content.Context
import android.content.Intent
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.http.ApiContext
import cc.sovellus.vrcaa.helper.cookies
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val context: Context
) : ScreenModel {
    private val api = ApiContext(context)
    private val preferences = context.getSharedPreferences("vrcaa_prefs", 0)

    fun doLogout() {
        screenModelScope.launch {
            api.logout()
            val intent = Intent(context, PipelineService::class.java)
            context.stopService(intent)
            preferences.cookies = ""
        }
    }
}