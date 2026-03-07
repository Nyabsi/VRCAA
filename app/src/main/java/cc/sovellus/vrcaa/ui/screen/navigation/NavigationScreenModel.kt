/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.screen.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.bundleOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.activity.MainActivity
import cc.sovellus.vrcaa.api.vrchat.http.HttpClient
import cc.sovellus.vrcaa.extension.avatarProvider
import cc.sovellus.vrcaa.extension.avatarsAmount
import cc.sovellus.vrcaa.extension.groupsAmount
import cc.sovellus.vrcaa.extension.searchFeaturedWorlds
import cc.sovellus.vrcaa.extension.sortWorlds
import cc.sovellus.vrcaa.extension.usersAmount
import cc.sovellus.vrcaa.extension.worldsAmount
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.CacheManager
import cc.sovellus.vrcaa.manager.DatabaseManager
import cc.sovellus.vrcaa.manager.NotificationManager
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch


class NavigationScreenModel : ScreenModel {

    companion object {
        const val SEARCH_FILTER_MIN_COUNT = 50
        const val SEARCH_FILTER_MAX_COUNT = 100
        const val SEARCH_FILTER_SNAP_STEP = 5
    }

    private val context: Context = App.getContext()
    private val preferences: SharedPreferences = context.getSharedPreferences(App.PREFERENCES_NAME, Context.MODE_PRIVATE)

    var searchModeActivated = mutableStateOf(false)
    var searchText = mutableStateOf("")
    var searchHistory = DatabaseManager.readQueries()
    var hasNoInternet = mutableStateOf(false)
    var invalidSession = mutableStateOf(false)

    var featuredWorlds = mutableStateOf(preferences.searchFeaturedWorlds)
    var sortWorlds = mutableStateOf(preferences.sortWorlds)
    var worldsAmount = mutableIntStateOf(normalizeSearchCount(preferences.worldsAmount))
    var usersAmount = mutableIntStateOf(normalizeSearchCount(preferences.usersAmount))
    var groupsAmount = mutableIntStateOf(normalizeSearchCount(preferences.groupsAmount))
    var avatarsAmount = mutableIntStateOf(normalizeSearchCount(preferences.avatarsAmount))
    var avatarProvider = mutableStateOf(preferences.avatarProvider)

    val status = mutableStateOf("")
    val description = mutableStateOf("")
    val bio = mutableStateOf("")
    val bioLinks = mutableStateListOf("", "", "")
    val ageVerified = mutableStateOf(false)
    val verifiedStatus = mutableStateOf("")
    val pronouns = mutableStateOf("")

    val notificationsCount = mutableIntStateOf(0)

    private val apiListener = object : HttpClient.SessionListener {
        override fun onSessionInvalidate() {
            if (!invalidSession.value) {
                invalidSession.value = true
                val serviceIntent = Intent(context, PipelineService::class.java)
                context.stopService(serviceIntent)

                val bundle = bundleOf()
                bundle.putBoolean("INVALID_SESSION", true)

                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

    private val cacheListener = object : CacheManager.CacheListener {
        override fun endCacheRefresh() {
            getCurrentProfileValues()
        }
    }

    init {
        api.setSessionListener(apiListener)
        CacheManager.addListener(cacheListener)

        screenModelScope.launch {
            NotificationManager.notificationCountState.collect { count ->
                notificationsCount.intValue = count
            }
        }
    }

    fun addSearchHistory() {
        screenModelScope.launch {
            if (searchText.value.isNotEmpty()) {
                searchHistory.add(searchText.value)
                DatabaseManager.writeQuery(searchText.value)
            }
            clearSearchText()
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
        preferences.worldsAmount = SEARCH_FILTER_MIN_COUNT
        worldsAmount.intValue = SEARCH_FILTER_MIN_COUNT
        preferences.usersAmount = SEARCH_FILTER_MIN_COUNT
        usersAmount.intValue = SEARCH_FILTER_MIN_COUNT
        preferences.avatarsAmount = SEARCH_FILTER_MIN_COUNT
        avatarsAmount.intValue = SEARCH_FILTER_MIN_COUNT
        avatarProvider.value = "avtrdb"
        preferences.avatarProvider = "avtrdb"
        preferences.groupsAmount = SEARCH_FILTER_MIN_COUNT
        groupsAmount.intValue = SEARCH_FILTER_MIN_COUNT
    }

    fun applySettings() {
        worldsAmount.intValue = normalizeSearchCount(worldsAmount.intValue)
        usersAmount.intValue = normalizeSearchCount(usersAmount.intValue)
        groupsAmount.intValue = normalizeSearchCount(groupsAmount.intValue)
        avatarsAmount.intValue = normalizeSearchCount(avatarsAmount.intValue)

        preferences.searchFeaturedWorlds = featuredWorlds.value
        preferences.sortWorlds = sortWorlds.value
        preferences.worldsAmount = worldsAmount.intValue
        preferences.usersAmount = usersAmount.intValue
        preferences.groupsAmount = groupsAmount.intValue
        preferences.avatarsAmount = avatarsAmount.intValue
        preferences.avatarProvider = avatarProvider.value
    }

    private fun normalizeSearchCount(value: Int): Int {
        val clamped = value.coerceIn(SEARCH_FILTER_MIN_COUNT, SEARCH_FILTER_MAX_COUNT)
        val snappedSteps = ((clamped - SEARCH_FILTER_MIN_COUNT + SEARCH_FILTER_SNAP_STEP / 2) / SEARCH_FILTER_SNAP_STEP)
        return SEARCH_FILTER_MIN_COUNT + snappedSteps * SEARCH_FILTER_SNAP_STEP
    }

    fun getCurrentProfileValues() {
        CacheManager.getProfile()?.let {
            status.value = it.status
            description.value = it.statusDescription
            bio.value = it.bio
            pronouns.value = it.pronouns

            for (i in 1..it.bioLinks.size)
            {
                bioLinks[i - 1] = it.bioLinks[i - 1]
            }

            ageVerified.value = it.ageVerified
            verifiedStatus.value = it.ageVerificationStatus
        }
    }

    override fun onDispose() {
        CacheManager.removeListener(cacheListener)
        api.clearSessionListener()
        super.onDispose()
    }
}
