package cc.sovellus.vrcaa.ui.screen.avatars

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class AvatarsScreenModel : StateScreenModel<AvatarsScreenModel.AvatarsState>(AvatarsState.Init) {

    sealed class AvatarsState {
        data object Init : AvatarsState()
        data object Loading : AvatarsState()
        data object Empty : AvatarsState()
        data class Result(
            val avatars: ArrayList<Avatar>
        ) : AvatarsState()
    }

    private var avatars: ArrayList<Avatar> = arrayListOf()

    init {
        mutableState.value = AvatarsState.Loading
        fetchAvatars()
    }

    private fun fetchAvatars() {
        screenModelScope.launch {
            avatars = api.user.fetchOwnedAvatars()

            if (avatars.isEmpty())
                mutableState.value = AvatarsState.Empty
            else
                mutableState.value = AvatarsState.Result(avatars)
        }
    }
}