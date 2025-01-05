package cc.sovellus.vrcaa.ui.screen.search

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.search.SearchAvatar
import cc.sovellus.vrcaa.api.search.avtrdb.AvtrDbProvider
import cc.sovellus.vrcaa.api.search.justhparty.JustHPartyProvider
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)

    private val avtrDbProvider = AvtrDbProvider()
    private val justHPartyProvider = JustHPartyProvider()

    private var worldOffset = 0
    private val worldStateFlow = MutableStateFlow(mutableStateListOf<World>())

    var worldLimitReached = mutableStateOf(false)
    val worlds = worldStateFlow.asStateFlow()

    private var userOffset = 0
    private val userStateFlow = MutableStateFlow(mutableStateListOf<LimitedUser>())

    var userLimitReached = mutableStateOf(false)
    val users = userStateFlow.asStateFlow()

    private var avatarOffset = 0
    private val avatarStateFlow = MutableStateFlow(mutableStateListOf<SearchAvatar>())

    var avatarLimitReached = mutableStateOf(false)
    val avatars = avatarStateFlow.asStateFlow()

    private var groupOffset = 0
    private val groupStateFlow = MutableStateFlow(mutableStateListOf<Group>())

    var groupLimitReached = mutableStateOf(false)
    val groups = groupStateFlow.asStateFlow()

    var currentIndex = mutableIntStateOf(0)

    init {
        mutableState.value = SearchState.Loading
        getContent()
    }

    private fun getContent() {
        screenModelScope.launch {

            App.setLoadingText(R.string.loading_text_worlds)

            worldStateFlow.value = api.worlds.fetchWorldsByName(
                query = query,
                n = preferences.worldsAmount,
                sort = preferences.sortWorlds,
                offset = worldOffset
            ).toMutableStateList()

            App.setLoadingText(R.string.loading_text_users)

            userStateFlow.value = api.users.fetchUsersByName(
                query = query,
                n = preferences.usersAmount,
                offset = userOffset
            ).toMutableStateList()

            App.setLoadingText(R.string.loading_text_avatars)

            when (preferences.avatarProvider) {
                "avtrdb" -> {
                    val result = avtrDbProvider.search(
                        query = query,
                        n = preferences.avatarsAmount,
                        offset = avatarOffset
                    )
                    avatarLimitReached.value = result.first
                    avatarStateFlow.value = result.second.toMutableStateList()
                }
                "justhparty" -> {
                    avatarLimitReached.value = true // doesn't support pagination
                    avatarStateFlow.value = justHPartyProvider.search(query).toMutableStateList()
                }
            }

            App.setLoadingText(R.string.loading_text_groups)

            groupStateFlow.value = api.groups.fetchGroupsByName(
                query = query,
                n = preferences.groupsAmount,
                offset = groupOffset
            ).toMutableStateList()

            mutableState.value = SearchState.Result
        }
    }

    fun fetchMoreWorlds() {
        worldOffset += 50

        screenModelScope.launch {

            val worlds = api.worlds.fetchWorldsByName(
                query = query,
                n = preferences.worldsAmount,
                sort = preferences.sortWorlds,
                offset = worldOffset
            ).toMutableStateList()

            if (worlds.isEmpty())
                worldLimitReached.value = true
            else
                worldStateFlow.value += worlds
        }
    }

    fun fetchMoreUsers() {
        userOffset += 50

        screenModelScope.launch {

            val users = api.users.fetchUsersByName(
                query = query,
                n = preferences.usersAmount,
                offset = userOffset
            ).toMutableStateList()

            if (users.isEmpty())
                userLimitReached.value = true
            else
                userStateFlow.value += users
        }
    }

    fun fetchMoreAvatars() {
        avatarOffset += 1

        screenModelScope.launch {

            val result = avtrDbProvider.search(
                query = query,
                n = preferences.avatarsAmount,
                offset = avatarOffset
            )

            if (result.first)
                avatarLimitReached.value = true
            else
                avatarStateFlow.value += result.second.toMutableStateList()
        }
    }

    fun fetchMoreGroups() {
        groupOffset += 50

        screenModelScope.launch {

            val groups = api.groups.fetchGroupsByName(
                query = query,
                n = preferences.groupsAmount,
                offset = groupOffset
            ).toMutableStateList()

            if (groups.isEmpty())
                groupLimitReached.value = true
            else
                groupStateFlow.value += groups
        }
    }
}