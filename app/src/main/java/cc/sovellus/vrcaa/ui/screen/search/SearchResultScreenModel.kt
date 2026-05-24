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

package cc.sovellus.vrcaa.ui.screen.search

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.search.avtrdb.AvtrDbProvider
import cc.sovellus.vrcaa.api.search.avtrdb.models.SearchAvatar
import cc.sovellus.vrcaa.api.vrchat.http.models.Group
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.extension.avatarProvider
import cc.sovellus.vrcaa.extension.avatarsAmount
import cc.sovellus.vrcaa.extension.groupsAmount
import cc.sovellus.vrcaa.extension.sortWorlds
import cc.sovellus.vrcaa.extension.usersAmount
import cc.sovellus.vrcaa.extension.worldsAmount
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.ui.components.misc.SEARCH_FILTER_MAX_COUNT
import cc.sovellus.vrcaa.ui.components.misc.SEARCH_FILTER_MIN_COUNT
import cc.sovellus.vrcaa.ui.components.misc.SEARCH_FILTER_SNAP_STEP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchResultScreenModel(
    private val query: String
) : StateScreenModel<SearchResultScreenModel.SearchState>(SearchState.Init) {

    sealed class SearchState {
        data object Init : SearchState()
        data object Loading : SearchState()
        data object Result : SearchState()
    }

    private val context: Context = App.getContext()
    val preferences: SharedPreferences = context.getSharedPreferences(App.PREFERENCES_NAME, MODE_PRIVATE)

    private val avtrDbProvider = AvtrDbProvider()

    private var worldOffset = 0
    private val _worldStateFlow = MutableStateFlow(listOf<World>())
    private var worldStateFlow = MutableStateFlow(listOf<World>())

    var worldLimitReached = mutableStateOf(false)
    val worlds = worldStateFlow.asStateFlow()

    private var userOffset = 0
    private val userStateFlow = MutableStateFlow(listOf<LimitedUser>())

    var userLimitReached = mutableStateOf(false)
    val users = userStateFlow.asStateFlow()

    private var avatarOffset = 0
    private val _avatarStateFlow = MutableStateFlow(listOf<SearchAvatar>())
    private val avatarStateFlow = MutableStateFlow(listOf<SearchAvatar>())

    var avatarLimitReached = mutableStateOf(false)
    val avatars = avatarStateFlow.asStateFlow()

    private var groupOffset = 0
    private val groupStateFlow = MutableStateFlow(listOf<Group>())

    var groupLimitReached = mutableStateOf(false)
    val groups = groupStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    var worldsAmount = mutableIntStateOf(normalizeSearchCount(preferences.worldsAmount))
    var usersAmount = mutableIntStateOf(normalizeSearchCount(preferences.usersAmount))
    var groupsAmount = mutableIntStateOf(normalizeSearchCount(preferences.groupsAmount))
    var avatarsAmount = mutableIntStateOf(normalizeSearchCount(preferences.avatarsAmount))

    val worldPlatformFilterSelection = mutableStateOf(listOf("PC", "Android"))
    val worldContentFilterSelection = mutableStateOf(listOf<String>())

    val avatarPlatformFilterSelection = mutableStateOf(listOf("PC", "Android"))
    val avatarPerformanceFilterSelection = mutableStateOf(listOf("Very Poor", "Poor", "Medium", "Good", "Excellent"))
    val avatarContentFilterSelection = mutableStateOf(listOf<String>())

    init {
        mutableState.value = SearchState.Loading
        getContent()
    }

    private fun normalizeSearchCount(value: Int): Int {
        val clamped = value.coerceIn(SEARCH_FILTER_MIN_COUNT, SEARCH_FILTER_MAX_COUNT)
        val snappedSteps = ((clamped - SEARCH_FILTER_MIN_COUNT + SEARCH_FILTER_SNAP_STEP / 2) / SEARCH_FILTER_SNAP_STEP)
        return SEARCH_FILTER_MIN_COUNT + snappedSteps * SEARCH_FILTER_SNAP_STEP
    }

    private fun getContent() {
        screenModelScope.launch {

            App.setLoadingText(R.string.loading_text_worlds)

            _worldStateFlow.value = api.worlds.fetchWorldsByName(
                query = query,
                n = preferences.worldsAmount,
                sort = preferences.sortWorlds,
                offset = worldOffset,
                tags = emptyList(),
                notags = emptyList(),
            )

            filterWorlds()

            App.setLoadingText(R.string.loading_text_users)

            userStateFlow.value = api.users.fetchUsersByName(
                query = query,
                n = preferences.usersAmount,
                offset = userOffset
            )

            App.setLoadingText(R.string.loading_text_avatars)

            when (preferences.avatarProvider) {
                "avtrdb" -> {
                    val result = avtrDbProvider.search(
                        query = query,
                        n = preferences.avatarsAmount,
                        offset = avatarOffset
                    )
                    avatarLimitReached.value = result.first
                    _avatarStateFlow.value = result.second
                    filterAvatars()
                }
            }

            App.setLoadingText(R.string.loading_text_groups)

            groupStateFlow.value = api.groups.fetchGroupsByName(
                query = query,
                n = preferences.groupsAmount,
                offset = groupOffset
            )

            mutableState.value = SearchState.Result
        }
    }

    fun fetchMoreWorlds() {
        worldOffset += preferences.worldsAmount

        val contentFilterMap = mapOf(
            "Sexually Suggestive" to "content_sex",
            "Adult Language and Themes" to "content_adult",
            "Graphic Violence" to "content_violence",
            "Excessive Gore" to "content_gore",
            "Extreme Horror" to "content_horror"
        )

        val contentFilters = worldContentFilterSelection.value
            .mapNotNull { contentFilterMap[it] }

        screenModelScope.launch {

            val worlds = api.worlds.fetchWorldsByName(
                query = query,
                n = preferences.worldsAmount,
                sort = preferences.sortWorlds,
                offset = worldOffset,
                tags = contentFilters,
                notags = emptyList(),
            )

            if (worlds.isEmpty()) {
                worldLimitReached.value = true
            }
            else {
                if (worldsContainsFilterRequirements(worlds)) {
                    _worldStateFlow.value += worlds
                    filterWorlds()
                } else {
                    fetchMoreWorlds()
                }
            }
        }
    }

    fun fetchMoreUsers() {
        userOffset += preferences.usersAmount

        screenModelScope.launch {

            val users = api.users.fetchUsersByName(
                query = query,
                n = preferences.usersAmount,
                offset = userOffset
            )

            if (users.isEmpty())
                userLimitReached.value = true
            else
                userStateFlow.value += users
        }
    }

    fun fetchMoreAvatars() {
        avatarOffset += preferences.avatarsAmount

        screenModelScope.launch {

            val result = avtrDbProvider.search(
                query = query,
                n = preferences.avatarsAmount,
                offset = avatarOffset
            )

            if (result.first)
                avatarLimitReached.value = true
            else {
                if (avatarContainsFilterRequirements(result.second)) {
                    _avatarStateFlow.value += result.second
                    filterAvatars()
                } else {
                    fetchMoreAvatars()
                }
            }
        }
    }

    fun fetchMoreGroups() {
        groupOffset += preferences.groupsAmount

        screenModelScope.launch {

            val groups = api.groups.fetchGroupsByName(
                query = query,
                n = preferences.groupsAmount,
                offset = groupOffset
            )

            if (groups.isEmpty())
                groupLimitReached.value = true
            else
                groupStateFlow.value += groups
        }
    }

    fun filterWorlds() {

        val platformFilterMap = mapOf(
            "standalonewindows" to "PC",
            "android" to "Android",
            "ios" to "iOS"
        )

        val contentFilterMap = mapOf(
            "content_sex" to "Sexually Suggestive",
            "content_adult" to "Adult Language and Themes",
            "content_violence" to "Graphic Violence",
            "content_gore" to "Excessive Gore",
            "content_horror" to "Extreme Horror"
        )

        val filtered = _worldStateFlow.value.filter { world ->

            val worldPlatforms = world.unityPackages
                .filter { it.variant == null }
                .mapNotNull { platformFilterMap[it.platform] }

            val contentFilters = world.tags
                .mapNotNull { contentFilterMap[it] }

            worldPlatformFilterSelection.value.all { it in worldPlatforms } &&
            worldContentFilterSelection.value.all { it in contentFilters }
        }

        worldsAmount.intValue = normalizeSearchCount(worldsAmount.intValue)
        preferences.worldsAmount = worldsAmount.intValue
        worldStateFlow.value = filtered.distinctBy { it.id }
    }

    fun worldsContainsFilterRequirements(worlds: List<World>): Boolean {

        val filterMap = mapOf(
            "standalonewindows" to "PC",
            "android" to "Android",
            "ios" to "iOS"
        )

        val contentFilterMap = mapOf(
            "Sexually Suggestive" to "content_sex",
            "Adult Language and Themes" to "content_adult",
            "Graphic Violence" to "content_violence",
            "Excessive Gore" to "content_gore",
            "Extreme Horror" to "content_horror"
        )

        val filtered = worlds.filter { world ->
            val worldPlatforms = world.unityPackages
                .filter { it.variant == null }
                .mapNotNull { filterMap[it.platform] }

            val contentFilters = world.tags
                .mapNotNull { contentFilterMap[it] }

            worldPlatformFilterSelection.value.all { it in worldPlatforms } &&
            worldContentFilterSelection.value.all { it in contentFilters }
        }

        return filtered.isNotEmpty()
    }

    fun resetWorldFilters() {
        worldPlatformFilterSelection.value = listOf("PC", "Android")
        worldContentFilterSelection.value = listOf()
        worldsAmount.intValue = SEARCH_FILTER_MIN_COUNT
        preferences.worldsAmount = worldsAmount.intValue
        filterWorlds()
    }

    fun filterAvatars() {

        val platformFilterMap = mapOf(
            "pc" to "PC",
            "android" to "Android",
            "ios" to "iOS"
        )

        val contentFilterMap = mapOf(
            "sex" to "Sexually Suggestive",
            "adult" to "Adult Language and Themes",
            "violence" to "Graphic Violence",
            "gore" to "Excessive Gore",
            "horror" to "Extreme Horror"
        )

        val performanceFilterMap = mapOf(
            "VeryPoor" to "Very Poor",
            "Poor" to "Poor",
            "Medium" to "Medium",
            "Good" to "Good",
            "Excellent" to "Excellent",
        )

        val filtered = _avatarStateFlow.value.filter { avatar ->

            val worldPlatforms = avatar.compatibility
                .mapNotNull { platformFilterMap[it] }

            val contentFilters = avatar.tags.contentTags
                .mapNotNull { contentFilterMap[it] }

            val performanceList = listOf(avatar.performance.pcRating, avatar.performance.androidRating, avatar.performance.iosRating)
            val performanceFilter = performanceList
                .filter { it != "" }
                .mapNotNull { performanceFilterMap[it] }

            avatarPlatformFilterSelection.value.all { it in worldPlatforms } &&
            avatarContentFilterSelection.value.all { it in contentFilters } &&
            avatarPerformanceFilterSelection.value.any { it in performanceFilter }
        }

        avatarsAmount.intValue = normalizeSearchCount(avatarsAmount.intValue)
        preferences.avatarsAmount = avatarsAmount.intValue
        avatarStateFlow.value = filtered.distinctBy { it.vrcId }
    }

    fun avatarContainsFilterRequirements(avatars: List<SearchAvatar>): Boolean {

        val platformFilterMap = mapOf(
            "pc" to "PC",
            "android" to "Android",
            "ios" to "iOS"
        )

        val contentFilterMap = mapOf(
            "sex" to "Sexually Suggestive",
            "adult" to "Adult Language and Themes",
            "violence" to "Graphic Violence",
            "gore" to "Excessive Gore",
            "horror" to "Extreme Horror"
        )

        val performanceFilterMap = mapOf(
            "VeryPoor" to "Very Poor",
            "Poor" to "Poor",
            "Medium" to "Medium",
            "Good" to "Good",
            "Excellent" to "Excellent",
        )

        val filtered = avatars.filter { avatar ->

            val worldPlatforms = avatar.compatibility
                .mapNotNull { platformFilterMap[it] }

            val contentFilters = avatar.tags.contentTags
                .mapNotNull { contentFilterMap[it] }

            val performanceList = listOf(avatar.performance.pcRating, avatar.performance.androidRating, avatar.performance.iosRating)
            val performanceFilter = performanceList
                .filter { it != "" }
                .mapNotNull { performanceFilterMap[it] }

            avatarPlatformFilterSelection.value.all { it in worldPlatforms } &&
                    avatarContentFilterSelection.value.all { it in contentFilters } &&
                    avatarPerformanceFilterSelection.value.any { it in performanceFilter }
        }

        return filtered.isNotEmpty()
    }

    fun resetAvatarFilters() {
        avatarPlatformFilterSelection.value = listOf("PC", "Android")
        avatarPerformanceFilterSelection.value = listOf("Very Poor", "Poor", "Medium", "Good", "Excellent")
        avatarContentFilterSelection.value = listOf()
        avatarsAmount.intValue = SEARCH_FILTER_MIN_COUNT
        preferences.avatarsAmount = avatarsAmount.intValue
        filterAvatars()
    }

    fun filterUsers() {
        usersAmount.intValue = normalizeSearchCount(usersAmount.intValue)
        preferences.usersAmount = usersAmount.intValue
    }

    fun resetUserFilters() {
        usersAmount.intValue = SEARCH_FILTER_MIN_COUNT
        preferences.usersAmount = usersAmount.intValue
        filterUsers()
    }

    fun filterGroups() {
        groupsAmount.intValue = normalizeSearchCount(groupsAmount.intValue)
        preferences.groupsAmount = groupsAmount.intValue
    }

    fun resetGroupFilters() {
        groupsAmount.intValue = SEARCH_FILTER_MIN_COUNT
        preferences.groupsAmount = groupsAmount.intValue
        filterGroups()
    }
}