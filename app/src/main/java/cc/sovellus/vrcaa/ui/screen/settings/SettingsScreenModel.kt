package cc.sovellus.vrcaa.ui.screen.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.helper.isMyServiceRunning
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val context: Context
) : ScreenModel {
    private val api = ApiContext(context)

    @SuppressLint("ApplySharedPref")
    fun doLogout() {
        screenModelScope.launch {
            api.logout()

            if (context.isMyServiceRunning(PipelineService::class.java)) {
                val intent = Intent(context, PipelineService::class.java)
                context.stopService(intent)
            }

            val editor = context.getSharedPreferences("vrcaa_prefs", 0).edit()
            editor.clear()
            editor.commit()
        }
    }
}