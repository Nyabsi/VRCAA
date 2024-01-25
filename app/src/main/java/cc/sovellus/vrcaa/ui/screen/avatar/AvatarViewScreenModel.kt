package cc.sovellus.vrcaa.ui.screen.avatar

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import kotlinx.coroutines.launch

class AvatarViewScreenModel(
    context: Context
) : ScreenModel {
    private val api = ApiContext(context)
    val isMenuExpanded = mutableStateOf(false)

    fun selectAvatar(avatarId: String) {
        screenModelScope.launch {
            // Really, just hope it goes OK!
            api.selectAvatar(avatarId)
        }
    }
}