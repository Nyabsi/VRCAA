package cc.sovellus.vrcaa.ui.screen.avatars

import android.content.Context
import android.widget.Toast
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Avatar
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FavoriteManager
import kotlinx.coroutines.launch

sealed class AvatarsState {
    data object Init : AvatarsState()
    data object Loading : AvatarsState()
    data class Result(
        val avatars: ArrayList<Avatar>
    ) : AvatarsState()
}

class AvatarsScreenModel : StateScreenModel<AvatarsState>(AvatarsState.Init) {

    private var avatars: ArrayList<Avatar> = arrayListOf()

    init {
        mutableState.value = AvatarsState.Loading
        fetchAvatars()
    }

    private fun fetchAvatars() {
        screenModelScope.launch {
            avatars = api.getOwnAvatars()
            mutableState.value = AvatarsState.Result(avatars)
        }
    }
}