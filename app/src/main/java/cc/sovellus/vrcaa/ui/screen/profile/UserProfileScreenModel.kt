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

package cc.sovellus.vrcaa.ui.screen.profile

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.search.avtrdb.AvtrDbProvider
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites.FavoriteType
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.helper.ApiHelper
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.manager.FavoriteManager.FavoriteMetadata
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.collections.set

class UserProfileScreenModel(
    private val userId: String
) : StateScreenModel<UserProfileScreenModel.UserProfileState>(UserProfileState.Init) {

    sealed class UserProfileState {
        data object Init : UserProfileState()
        data object Loading : UserProfileState()
        data object Failure : UserProfileState()
        data class Result(
            val profile: LimitedUser?,
            val instance: Instance?
        ) : UserProfileState()
    }

    private val avatarProvider = AvtrDbProvider()

    private var profile: LimitedUser? = null
    private var instance: Instance? = null

    init {
        mutableState.value = UserProfileState.Loading
        App.setLoadingText(R.string.loading_text_user)
        fetchProfile()
    }

    private fun fetchProfile() {
        screenModelScope.launch {
            api.users.fetchUserByUserId(userId)?.let {
                it.location.let { location ->
                    if (it.isFriend &&
                        location.isNotEmpty() &&
                        location != "private" &&
                        location != "traveling" &&
                        location != "offline") {
                        instance = api.instances.fetchInstance(location)
                    }
                }
                profile = it

                mutableState.value = UserProfileState.Result(profile, instance)
            } ?: run {
                mutableState.value = UserProfileState.Failure
            }
        }
    }

    fun findAvatar(callback: ((userId: String?) -> Unit?)) {
        screenModelScope.launch {
            profile?.let {
                val fileId = ApiHelper.extractFileIdFromUrl(it.currentAvatarImageUrl)

                if (fileId != null) {
                    api.files.fetchMetadataByFileId(fileId)?.let { metadata ->

                        val name = metadata.name.split(" - ")

                        if (name.size > 1) {
                            val (_, nameAvatars) = avatarProvider.search(name[1])
                            if (nameAvatars.isNotEmpty()) {
                                for (avatar in nameAvatars) {
                                    val avatarFileId = ApiHelper.extractFileIdFromUrl(avatar.imageUrl)
                                    if (avatarFileId == fileId) {
                                        callback(avatar.vrcId)
                                        return@launch
                                    }
                                }
                            }

                            // fallback to using author search
                            val (_, authorAvatars) = avatarProvider.search(metadata.ownerId)
                            if (authorAvatars.isNotEmpty()) {
                                for (avatar in authorAvatars) {
                                    val avatarFileId = ApiHelper.extractFileIdFromUrl(avatar.imageUrl)
                                    if (avatarFileId == fileId) {
                                        callback(avatar.vrcId)
                                        return@launch
                                    }
                                }
                            }

                            callback(null)
                        }
                    }
                }
            }
        }
    }

    fun inviteToFriend(intent: String) {
        screenModelScope.launch {
            api.instances.selfInvite(intent)
        }
    }

    fun removeFavorite(callback: (result: Boolean) -> Unit) {
        screenModelScope.launch {
            profile?.let {
                val result = FavoriteManager.removeFavorite(IFavorites.FavoriteType.FAVORITE_FRIEND, it.id)
                callback(result)
            }
        }
    }
}