package cc.sovellus.vrcaa.ui.screen.settings

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App

class SettingsScreenModel : ScreenModel {
    private val context: Context = App.getContext()
    val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
}