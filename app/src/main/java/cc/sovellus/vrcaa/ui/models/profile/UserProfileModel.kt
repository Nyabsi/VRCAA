package cc.sovellus.vrcaa.ui.models.profile

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class UserProfileScreenModel(
    private val context: Context,
    private val userId: String
) : StateScreenModel<UserProfileScreenModel.UserProfileState>(UserProfileState.Init) {

    sealed class UserProfileState {
        data object Init : UserProfileState()
        data object Loading : UserProfileState()
        data class Result(val profile: LimitedUser?, val instance: Instance?) : UserProfileState()
    }

    private var profile: LimitedUser? = null
    private var instance: Instance? = null

    init {
        mutableState.value = UserProfileState.Loading
        fetchProfile()
    }

    private fun fetchProfile() {
        screenModelScope.launch {
            api?.getUser(userId).let {
                if (it != null) {
                    it.location?.let { location ->
                        if (it.isFriend &&
                            location.isNotEmpty() &&
                            location != "private" &&
                            location != "traveling" &&
                            location != "offline") {
                            instance = api?.getInstance(location)
                        }
                    }
                }
                profile = it
            }

            mutableState.value = UserProfileState.Result(profile, instance)
        }
    }
}