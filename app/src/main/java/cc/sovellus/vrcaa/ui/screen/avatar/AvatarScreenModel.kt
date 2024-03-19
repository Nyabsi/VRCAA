package cc.sovellus.vrcaa.ui.screen.avatar

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.http.models.Avatar
import cc.sovellus.vrcaa.manager.ApiManager.api
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
    val once = mutableStateOf(false)

    private var avatar: Avatar? = null

    init {
        screenModelScope.launch {
            avatar = api.getAvatar(avatarId)
            mutableState.value = AvatarState.Result(avatar)
        }
    }

    fun selectAvatar() {
        screenModelScope.launch {
            if (avatar == null) {
                Toast.makeText(
                    context,
                    "Failed to fetch avatar due to API error, try again.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                avatar?.id?.let { api.selectAvatar(it) }
            }
        }
    }
}