package cc.sovellus.vrcaa.ui.screen.avatar

import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import kotlinx.coroutines.launch

class AvatarViewScreenModel(
    private val context: Context
) : ScreenModel {
    private val api = ApiContext(context)

    fun selectAvatar(avatarId: String) {
        screenModelScope.launch {
            // Really, just hope it goes OK!
            api.selectAvatar(avatarId)
        }
    }
}