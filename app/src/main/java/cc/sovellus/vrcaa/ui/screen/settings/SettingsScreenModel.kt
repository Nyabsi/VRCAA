package cc.sovellus.vrcaa.ui.screen.settings

import android.annotation.SuppressLint
import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import kotlinx.coroutines.launch

class SettingsScreenModel(
    private val api: ApiContext,
    private val context: Context
) : ScreenModel {
    @SuppressLint("ApplySharedPref")
    fun doLogout() {
        screenModelScope.launch {
            api.logout()
            val editor = context.getSharedPreferences("vrcaa_prefs", 0).edit()
            editor.clear()
            editor.commit()
        }
    }
}