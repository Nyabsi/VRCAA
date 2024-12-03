package cc.sovellus.vrcaa.ui.screen.avatar

import android.content.Context
import android.widget.Toast
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Avatar
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FavoriteManager
import kotlinx.coroutines.launch

class AvatarScreenModel(
    private val avatarId: String
) : StateScreenModel<AvatarScreenModel.AvatarState>(AvatarState.Init) {

    sealed class AvatarState {
        data object Init : AvatarState()
        data object Loading : AvatarState()
        data object Failure : AvatarState()
        data class Result(
            val avatar: Avatar
        ) : AvatarState()
    }

    private val context: Context = App.getContext()
    private lateinit var avatar: Avatar

    init {
        mutableState.value = AvatarState.Loading
        fetchAvatar(avatarId)
    }

    private fun fetchAvatar(avatarId: String) {
        screenModelScope.launch {
            App.setLoadingText(R.string.loading_text_avatar)
            val result = api.getAvatar(avatarId)
            result?.let { avtr ->
                avatar = avtr
                mutableState.value = AvatarState.Result(avtr)
            } ?: run {
                mutableState.value = AvatarState.Failure
            }
        }
    }

    fun selectAvatar() {
        screenModelScope.launch {
            avatar.id.let { id ->
                api.selectAvatar(id)

                Toast.makeText(
                    context,
                    context.getString(R.string.avatar_dropdown_toast_select_avatar),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun removeFavorite() {
        screenModelScope.launch {
            val result = FavoriteManager.removeFavorite("avatar", avatarId)
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