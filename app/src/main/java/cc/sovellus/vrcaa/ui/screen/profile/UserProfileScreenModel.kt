package cc.sovellus.vrcaa.ui.screen.profile

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.LimitedUser
import cc.sovellus.vrcaa.api.models.User
import kotlinx.coroutines.launch

class UserProfileScreenModel(
    private val api: ApiContext,
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
            profile.value = api.getUser(userId)
            mutableState.value = UserProfileState.Result(profile.value!!)
        }
    }
}