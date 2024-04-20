package cc.sovellus.vrcaa.ui.models.profile

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class ProfileScreenModel(
    private val context: Context
) : StateScreenModel<ProfileScreenModel.ProfileState>(ProfileState.Init) {

    sealed class ProfileState {
        data object Init : ProfileState()
        data object Loading : ProfileState()
        data class Result(val profile: User?) : ProfileState()
    }

    private var profile: User? = null

    init {
        mutableState.value = ProfileState.Loading
        fetchProfile()
    }

    private fun fetchProfile() {
        screenModelScope.launch {
            profile = api?.getSelf()
            mutableState.value = ProfileState.Result(profile)
        }
    }
}