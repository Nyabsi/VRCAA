package cc.sovellus.vrcaa.ui.screen.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.activity.MainActivity
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val context: Context
) : ScreenModel {
    private val preferences = context.getSharedPreferences("vrcaa_prefs", 0)

    fun doLogout() {
        screenModelScope.launch {
            var intent = Intent(context, PipelineService::class.java)
            context.stopService(intent)

            api.logout()

            preferences.authToken = ""

            val bundle = bundleOf()
            bundle.putBoolean("TERMINATE_SESSION", true)

            intent = Intent(context, MainActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)

            if (context is Activity) {
                context.finish()
            }
        }
    }
}