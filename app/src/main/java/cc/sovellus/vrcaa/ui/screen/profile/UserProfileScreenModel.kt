package cc.sovellus.vrcaa.ui.screen.profile

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.http.models.Instance
import cc.sovellus.vrcaa.api.http.models.LimitedUser
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

    val isMenuExpanded = mutableStateOf(false)

    init {
        mutableState.value = UserProfileState.Loading
        fetchProfile()
    }

    private fun fetchProfile() {
        screenModelScope.launch {
            api.getUser(userId).let {
                if (it != null) {
                    if (it.isFriend &&
                        it.location.isNotEmpty() &&
                        it.location != "private" &&
                        it.location != "traveling" &&
                        it.location != "offline") {
                        instance = api.getInstance(it.location)
                    }
                }
                profile = it
            }

            mutableState.value = UserProfileState.Result(profile, instance)
        }
    }
}