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

sealed class AvatarState {
    data object Init : AvatarState()
    data object Loading : AvatarState()
    data class Result(
        val avatar: Avatar?
    ) : AvatarState()
}

class AvatarScreenModel(
    private val context: Context,
    private val avatarId: String
) : StateScreenModel<AvatarState>(AvatarState.Init) {

    private var avatar: Avatar? = null

    init {
        mutableState.value = AvatarState.Loading
        fetchAvatar(avatarId)
    }

    private fun fetchAvatar(avatarId: String) {
        screenModelScope.launch {
            App.setLoadingText(R.string.loading_text_avatar)
            avatar = api.getAvatar(avatarId)
            mutableState.value = AvatarState.Result(avatar)
        }
    }

    fun selectAvatar(): Boolean {
        screenModelScope.launch {
            if (avatar == null) {
                Toast.makeText(
                    context,
                    context.getString(R.string.api_avatar_select_failed),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                avatar?.id?.let { api.selectAvatar(it) }
            }
        }
        return avatar != null
    }

    fun removeFavorite(callback: (result: Boolean) -> Unit) {
        screenModelScope.launch {
            val result = FavoriteManager.removeFavorite("avatar", avatarId)
            callback(result)
        }
    }
}