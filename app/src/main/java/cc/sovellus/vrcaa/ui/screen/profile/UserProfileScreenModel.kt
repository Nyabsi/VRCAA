package cc.sovellus.vrcaa.ui.screen.profile

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.http.models.Instance
import cc.sovellus.vrcaa.api.http.models.LimitedUser
import cc.sovellus.vrcaa.helper.api
import kotlinx.coroutines.launch

class UserProfileScreenModel(
    private val context: Context,
    private val userId: String
) : StateScreenModel<UserProfileScreenModel.UserProfileState>(UserProfileState.Init) {

    sealed class UserProfileState {
        data object Init : UserProfileState()
        data object Loading : UserProfileState()
        data class Result(val profile: LimitedUser, val instance: Instance?) : UserProfileState()
    }

    private var profile = mutableStateOf<LimitedUser?>(null)
    private var instance = mutableStateOf<Instance?>(null)

    val isMenuExpanded = mutableStateOf(false)

    init {
        mutableState.value = UserProfileState.Loading
        getProfile()
    }

    private fun getProfile() {
        screenModelScope.launch {
            profile.value = context.api.get().getUser(userId)
            if (profile.value != null && profile.value!!.isFriend && profile.value!!.location.isNotEmpty() && profile.value!!.location != "private" && profile.value!!.location != "traveling") {
                instance.value = context.api.get().getInstance(profile.value!!.location)
            }
            mutableState.value = UserProfileState.Result(profile.value!!, instance.value)
        }
    }

    suspend fun getInstance(intent: String): Instance? {
        return context.api.get().getInstance(intent)
    }
}