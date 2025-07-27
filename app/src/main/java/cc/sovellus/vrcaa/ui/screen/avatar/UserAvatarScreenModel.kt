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

package cc.sovellus.vrcaa.ui.screen.avatar

import android.content.Context
import android.widget.Toast
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.interfaces.IFavorites
import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FavoriteManager
import kotlinx.coroutines.launch

class UserAvatarScreenModel(
    private val avatar: Avatar
) : StateScreenModel<UserAvatarScreenModel.UserAvatarState>(UserAvatarState.Init) {

    sealed class UserAvatarState {
        data object Init : UserAvatarState()
        data object Loading : UserAvatarState()
        data class Result(
            val avatar: Avatar
        ) : UserAvatarState()
    }

    private val context: Context = App.getContext()

    init {
        mutableState.value = UserAvatarState.Result(avatar)
    }

    fun selectAvatar() {
        screenModelScope.launch {
            avatar.id.let { id ->
                api.avatars.selectAvatarById(id).let {
                    Toast.makeText(
                        context,
                        context.getString(R.string.avatar_dropdown_toast_select_avatar),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun removeFavorite() {
        screenModelScope.launch {
            val result =
                FavoriteManager.removeFavorite(IFavorites.FavoriteType.FAVORITE_AVATAR, avatar.id)
            if (result) {
                Toast.makeText(
                    context,
                    context.getString(R.string.favorite_toast_favorite_removed)
                        .format(avatar.name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.favorite_toast_favorite_removed_failed)
                        .format(avatar.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}