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
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
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
import cc.sovellus.vrcaa.manager.FeedManager
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class NavigationScreenModel : ScreenModel {

    private val context: Context = App.getContext()
    private val preferences: SharedPreferences = context.getSharedPreferences(App.PREFERENCES_NAME, Context.MODE_PRIVATE)

    var searchModeActivated = mutableStateOf(false)
    var searchText = mutableStateOf("")
    var searchHistory = DatabaseManager.readQueries()
    var hasNoInternet = mutableStateOf(false)
    var invalidSession = mutableStateOf(false)

    var featuredWorlds = mutableStateOf(preferences.searchFeaturedWorlds)
    var sortWorlds = mutableStateOf(preferences.sortWorlds)
    var worldsAmount = mutableIntStateOf(preferences.worldsAmount)
    var usersAmount = mutableIntStateOf(preferences.usersAmount)
    var groupsAmount = mutableIntStateOf(preferences.groupsAmount)
    var avatarsAmount = mutableIntStateOf(preferences.avatarsAmount)
    var avatarProvider = mutableStateOf(preferences.avatarProvider)

    val status = mutableStateOf("")
    val description = mutableStateOf("")
    val bio = mutableStateOf("")
    val bioLinks = mutableStateListOf("", "", "")
    val ageVerified = mutableStateOf(false)
    val verifiedStatus = mutableStateOf("")
    val pronouns = mutableStateOf("")

    var feedFilterQuery = mutableStateOf("")
    var showFilteredFeed = mutableStateOf(false)

    private var filteredFeedStateFlow = MutableStateFlow(mutableStateListOf<FeedManager.Feed>())
    var filteredFeed = filteredFeedStateFlow.asStateFlow()

    private val apiListener = object : HttpClient.SessionListener {
        override fun onSessionInvalidate() {
            if (!invalidSession.value) {
                invalidSession.value = true
                val serviceIntent = Intent(context, PipelineService::class.java)
                context.stopService(serviceIntent)

                val bundle = bundleOf()
                bundle.putBoolean("INVALID_SESSION", true)

                val intent = Intent(context, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
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
        preferences.worldsAmount = 50
        worldsAmount.intValue = 50
        preferences.usersAmount = 50
        usersAmount.intValue = 50
        preferences.avatarsAmount = 50
        avatarsAmount.intValue = 50
        avatarProvider.value = "avtrdb"
        preferences.avatarProvider = "avtrdb"
        preferences.groupsAmount = 50
        groupsAmount.intValue = 50
    }

    fun applySettings() {
        preferences.searchFeaturedWorlds = featuredWorlds.value
        preferences.sortWorlds = sortWorlds.value
        preferences.worldsAmount = worldsAmount.intValue
        preferences.usersAmount = usersAmount.intValue
        preferences.groupsAmount = groupsAmount.intValue
        preferences.avatarsAmount = avatarsAmount.intValue
        preferences.avatarProvider = avatarProvider.value
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

    fun filterFeed() {
        val filteredFeed = FeedManager.getFeed().filter { feed ->
            feed.friendName.contains(feedFilterQuery.value, ignoreCase = true) || (feed.travelDestination.contains(feedFilterQuery.value, ignoreCase = true) && feed.type == FeedManager.FeedType.FRIEND_FEED_LOCATION) || (feed.avatarName.contains(feedFilterQuery.value, ignoreCase = true) && feed.type == FeedManager.FeedType.FRIEND_FEED_AVATAR)
        }

        filteredFeedStateFlow.value = filteredFeed.toMutableStateList()
    }
}
