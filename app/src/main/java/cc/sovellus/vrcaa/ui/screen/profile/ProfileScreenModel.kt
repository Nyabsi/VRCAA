package cc.sovellus.vrcaa.ui.screen.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cc.sovellus.vrcaa.api.vrchat.models.User
import cc.sovellus.vrcaa.manager.ApiManager.cache

sealed class ProfileState {
    data object Init : ProfileState()
    data object Loading : ProfileState()
    data class Result(val profile: User?) : ProfileState()
}

class ProfileScreenModel : StateScreenModel<ProfileState>(ProfileState.Init) {

    private var profile: User? = cache.getProfile()

    init {
        mutableState.value = ProfileState.Result(profile)
    }
}