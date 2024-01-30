package cc.sovellus.vrcaa.ui.screen.avatar

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.Avatar
import kotlinx.coroutines.launch

class AvatarScreenModel(
    context: Context,
    avatarId: String
) : StateScreenModel<AvatarScreenModel.AvatarState>(AvatarState.Init) {
    private val api = ApiContext(context)

    sealed class AvatarState {
        data object Init : AvatarState()
        data object Loading : AvatarState()
        data class Result(
            val avatar: Avatar?
        ) : AvatarState()
    }

    val isMenuExpanded = mutableStateOf(false)
    var avatar: Avatar? = null
    val once = mutableStateOf(false)

    init {
        screenModelScope.launch {
            avatar = api.getAvatar(avatarId)
            mutableState.value = AvatarState.Result(avatar)
        }
    }

    fun selectAvatar(avatarId: String) {
        screenModelScope.launch {
            api.selectAvatar(avatarId)
        }
    }
}