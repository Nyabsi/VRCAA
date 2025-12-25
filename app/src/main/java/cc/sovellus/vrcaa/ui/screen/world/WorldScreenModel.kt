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

package cc.sovellus.vrcaa.ui.screen.world

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.models.Friend
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.http.models.World
import cc.sovellus.vrcaa.helper.LocationHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class WorldScreenModel(
    private val id: String,
) : StateScreenModel<WorldScreenModel.WorldInfoState>(WorldInfoState.Init) {

    private val context: Context = App.getContext()

    data class InstanceWithFriends(
        val instance: Instance?,
        var friends: MutableList<Friend>,
        val creator: LimitedUser?
    )

    sealed class WorldInfoState {
        data object Init : WorldInfoState()
        data object Loading : WorldInfoState()
        data object Failure : WorldInfoState()
        data class Result(
            val world: World,
            val instances: List<Pair<String, InstanceWithFriends>>
        ) : WorldInfoState()
    }

    var currentTabIndex = mutableIntStateOf(0)
    var selectedInstanceId = mutableStateOf("")

    init {
        mutableState.value = WorldInfoState.Loading
        App.setLoadingText(R.string.loading_text_world)
        fetchWorld()
    }

    private fun fetchWorld() {
        screenModelScope.launch {
            val result = api.worlds.fetchWorldByWorldId(id)

            result?.let { world ->

                App.setLoadingText(R.string.loading_text_instances)

                val instances = world.instances.map { instance ->
                    instance[0] as String
                }.distinct().map { id ->
                    async {
                        val instance = api.instances.fetchInstance("${world.id}:${id}")
                        instance?.let {
                            Pair(id, InstanceWithFriends(instance, mutableListOf(), api.users.fetchUserByUserId(instance.ownerId)))
                        } ?: run {
                            Pair(id, InstanceWithFriends(null, mutableListOf(), null))
                        }
                    }
                }.awaitAll()


                val injectedUserLocations: MutableList<Pair<String, InstanceWithFriends>> = mutableListOf()
                injectedUserLocations.addAll(instances)

                for (friend in FriendManager.getFriends()) {
                    val location = LocationHelper.parseLocationInfo(friend.location)
                    if (location.worldId == id) {
                        val existingLocation = injectedUserLocations.find { it.first == location.instanceId }
                        existingLocation?.let {
                            existingLocation.second.friends.add(friend)
                        } ?: run {
                            val instance = api.instances.fetchInstance(friend.location)
                            instance?.let {
                                injectedUserLocations.add(Pair(location.instanceId, InstanceWithFriends(instance, mutableListOf(friend), api.users.fetchUserByUserId(instance.ownerId))))
                            } ?: run {
                                injectedUserLocations.add(Pair(location.instanceId, InstanceWithFriends(null, mutableListOf(friend), null)))
                            }
                        }
                    }
                }

                injectedUserLocations.sortBy { it.second.friends.isEmpty() }
                mutableState.value = WorldInfoState.Result(world, injectedUserLocations.toList())
            } ?: run {
                mutableState.value = WorldInfoState.Failure
            }
        }
    }

    fun selfInvite() {
        screenModelScope.launch {
            api.instances.selfInvite(selectedInstanceId.value)
        }
    }

    fun removeFavorite(world: World) {
        screenModelScope.launch {
            val result = FavoriteManager.removeFavorite(IFavorites.FavoriteType.FAVORITE_WORLD, world.id)

            if (result) {
                Toast.makeText(
                    context,
                    context.getString(R.string.favorite_toast_favorite_removed)
                        .format(world.name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.favorite_toast_favorite_removed_failed)
                        .format(world.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}