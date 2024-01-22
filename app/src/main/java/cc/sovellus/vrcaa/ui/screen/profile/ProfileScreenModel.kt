package cc.sovellus.vrcaa.ui.screen.profile

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.api.models.Friends
import cc.sovellus.vrcaa.api.models.User
import cc.sovellus.vrcaa.ui.screen.friends.FriendsScreenModel
import kotlinx.coroutines.launch

class ProfileScreenModel(
    private val api: ApiContext
) : StateScreenModel<ProfileScreenModel.ProfileState>(ProfileState.Init) {

    sealed class ProfileState {
        data object Init : ProfileState()
        data object Loading : ProfileState()
        data class Result (val profile: User) : ProfileState()
    }

    private var profile = mutableStateOf<User?>(null)

    init {
        mutableState.value = ProfileState.Loading
        getProfile()
    }
    private fun getProfile() {
        screenModelScope.launch {
            profile.value = api.getSelf()
            mutableState.value = ProfileState.Result(profile.value!!)
        }
    }
}