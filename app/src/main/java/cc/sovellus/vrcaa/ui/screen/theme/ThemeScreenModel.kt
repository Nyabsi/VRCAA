package cc.sovellus.vrcaa.ui.screen.theme

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.minimalistMode

class ThemeScreenModel : ScreenModel {
    val preferences: SharedPreferences = App.getContext().getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
    val minimalistMode = mutableStateOf(preferences.minimalistMode)
    var currentIndex = mutableIntStateOf(preferences.currentThemeOption)
}