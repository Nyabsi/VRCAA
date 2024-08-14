package cc.sovellus.vrcaa.ui.screen.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.bundleOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.activity.MainActivity
import cc.sovellus.vrcaa.api.vrchat.VRChatApi
import cc.sovellus.vrcaa.extension.groupsAmount
import cc.sovellus.vrcaa.extension.searchFeaturedWorlds
import cc.sovellus.vrcaa.extension.sortWorlds
import cc.sovellus.vrcaa.extension.usersAmount
import cc.sovellus.vrcaa.extension.worldsAmount
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch


class NavigationScreenModel(
    private val context: Context
) : ScreenModel {

    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", Context.MODE_PRIVATE)

    var searchModeActivated = mutableStateOf(false)
    var searchText = mutableStateOf("")
    var searchHistory = mutableListOf<String>()
    var hasNoInternet = mutableStateOf(false)
    var invalidSession = mutableStateOf(false)

    var featuredWorlds = mutableStateOf(preferences.searchFeaturedWorlds)
    var sortWorlds = mutableStateOf(preferences.sortWorlds)
    var worldsAmount = mutableIntStateOf(preferences.worldsAmount)
    var usersAmount = mutableIntStateOf(preferences.usersAmount)
    var groupsAmount = mutableIntStateOf(preferences.groupsAmount)

    private val listener = object : VRChatApi.SessionListener {
        override fun onSessionInvalidate() {
            if (!invalidSession.value) {
                invalidSession.value = true

                val serviceIntent = Intent(context, PipelineService::class.java)
                context.stopService(serviceIntent)

                val bundle = bundleOf()
                bundle.putBoolean("INVALID_SESSION", true)

                val intent = Intent(context, MainActivity::class.java)
                intent.putExtras(bundle)
                context.startActivity(intent)

                if (context is Activity) {
                    context.finish()
                }
            }
        }

        override fun noInternet() {
            hasNoInternet.value = true
        }
    }

    init {
        api.setSessionListener(listener)
    }

    fun enterSearchMode() {
        screenModelScope.launch {
            searchModeActivated.value = true
        }
    }

    fun existSearchMode() {
        screenModelScope.launch {
            searchModeActivated.value = false
            if (searchText.value.isNotEmpty())
                searchHistory.add(searchText.value)
        }
    }

    fun clearSearchText() {
        searchText.value = ""
    }

    fun resetSettings() {
        preferences.searchFeaturedWorlds = false
        featuredWorlds.value = false
        preferences.sortWorlds = "relevance"
        sortWorlds.value = "relevance"
        preferences.worldsAmount = 50
        worldsAmount.intValue = 50
        preferences.usersAmount = 50
        usersAmount.intValue = 50
        preferences.groupsAmount = 50
        groupsAmount.intValue = 50

        Toast.makeText(
            context,
            "Reset settings.",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun applySettings() {
        preferences.searchFeaturedWorlds = featuredWorlds.value
        preferences.sortWorlds = sortWorlds.value
        preferences.worldsAmount = worldsAmount.intValue
        preferences.usersAmount = usersAmount.intValue
        preferences.groupsAmount = groupsAmount.intValue

        Toast.makeText(
            context,
            "Applied settings.",
            Toast.LENGTH_SHORT
        ).show()
    }
}