package cc.sovellus.vrcaa.ui.screen.navigationButtons

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.extension.homeTab
import cc.sovellus.vrcaa.extension.friendsTab
import cc.sovellus.vrcaa.extension.favoritesTab
import cc.sovellus.vrcaa.extension.FeedTab
import cc.sovellus.vrcaa.extension.settingsTab

class NavigationButtonSettingsModel : ScreenModel {
    val preferences: SharedPreferences = App.getContext().getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
    var currentIndexHo = mutableIntStateOf(preferences.homeTab)
    var currentIndexFr = mutableIntStateOf(preferences.friendsTab)
    var currentIndexFa = mutableIntStateOf(preferences.favoritesTab)
    var currentIndexFe = mutableIntStateOf(preferences.FeedTab)
    var currentIndexSe = mutableIntStateOf(preferences.settingsTab)

}