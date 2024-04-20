package cc.sovellus.vrcaa.ui.models.profile

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.justhparty.JustHPartyProvider
import cc.sovellus.vrcaa.api.vrchat.models.Instance
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
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

    private val avatarProvider = JustHPartyProvider()

    private var profile: LimitedUser? = null
    private var instance: Instance? = null

    init {
        mutableState.value = UserProfileState.Loading
        fetchProfile()
    }

    private fun fetchProfile() {
        screenModelScope.launch {
            api?.getUser(userId)?.let {
                it.location.let { location ->
                    if (it.isFriend &&
                        location.isNotEmpty() &&
                        location != "private" &&
                        location != "traveling" &&
                        location != "offline") {
                        instance = api?.getInstance(location)
                    }
                }
                profile = it
            }

            mutableState.value = UserProfileState.Result(profile, instance)
        }
    }

    fun findAvatar(callback: ((userId: String?) -> Unit?)) {
        screenModelScope.launch {
            profile?.let {
                val url = it.currentAvatarImageUrl
                val fileId = url.substring(url.indexOf("file_"), url.lastIndexOf("/file") - 2)

                api?.getFileMetadata(fileId)?.let { metadata ->
                    var name = metadata.name

                    name = name.substring(9) // skip first 9 characters, not required.
                    name = name.substring(0, name.indexOf('-') - 1)

                    val avatars = avatarProvider.search("search", name, 5000)
                    if (avatars != null) {
                       for (avatar in avatars) {
                           if (avatar.name == name && avatar.authorId == metadata.ownerId) {
                               callback(avatar.id)
                               return@launch
                           }
                       }
                    }
                    callback(null)
                }
            }
        }
    }
}