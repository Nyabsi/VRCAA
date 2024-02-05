package cc.sovellus.vrcaa.ui.screen.profile

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.models.LimitedUser
import cc.sovellus.vrcaa.helper.api
import kotlinx.coroutines.launch

class UserProfileScreenModel(
    private val context: Context,
    private val userId: String
) : StateScreenModel<UserProfileScreenModel.UserProfileState>(UserProfileState.Init) {

    sealed class UserProfileState {
        data object Init : UserProfileState()
        data object Loading : UserProfileState()
        data class Result (val profile: LimitedUser) : UserProfileState()
    }

    private var profile = mutableStateOf<LimitedUser?>(null)
    val isMenuExpanded = mutableStateOf(false)

    init {
        mutableState.value = UserProfileState.Loading
        getProfile()
    }
    private fun getProfile() {
        screenModelScope.launch {
            profile.value = context.api.get().getUser(userId)
            mutableState.value = UserProfileState.Result(profile.value!!)
        }
    }
}