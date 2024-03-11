package cc.sovellus.vrcaa.ui.screen.avatar

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.http.models.Avatar
import cc.sovellus.vrcaa.helper.api
import kotlinx.coroutines.launch

class AvatarScreenModel(
    private val context: Context,
    avatarId: String
) : StateScreenModel<AvatarScreenModel.AvatarState>(AvatarState.Init) {

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
            avatar = context.api.get().getAvatar(avatarId)
            mutableState.value = AvatarState.Result(avatar)
        }
    }

    fun selectAvatar() {
        screenModelScope.launch {
            context.api.get().selectAvatar(avatar!!.id)
        }
    }
}