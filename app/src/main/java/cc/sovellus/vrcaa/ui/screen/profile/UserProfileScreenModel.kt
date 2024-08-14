package cc.sovellus.vrcaa.ui.screen.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.justhparty.JustHPartyProvider
import cc.sovellus.vrcaa.api.vrchat.models.Instance
import cc.sovellus.vrcaa.api.vrchat.models.LimitedUser
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FavoriteManager
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.launch

sealed class UserProfileState {
    data object Init : UserProfileState()
    data object Loading : UserProfileState()
    data class Result(val profile: LimitedUser?, val instance: Instance?) : UserProfileState()
}

class UserProfileScreenModel(
    private val userId: String
) : StateScreenModel<UserProfileState>(UserProfileState.Init) {

    private val avatarProvider = JustHPartyProvider()

    private var profile: LimitedUser? = null
    private var instance: Instance? = null

    init {
        mutableState.value = UserProfileState.Loading
        fetchProfile()
    }

    private fun fetchProfile() {
        screenModelScope.launch {
            api.getUser(userId)?.let {
                it.location.let { location ->
                    if (it.isFriend &&
                        location.isNotEmpty() &&
                        location != "private" &&
                        location != "traveling" &&
                        location != "offline") {
                        instance = api.getInstance(location)
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

                api.getFileMetadata(fileId)?.let { metadata ->
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

    fun inviteToFriend(intent: String) {
        screenModelScope.launch {
            api.inviteSelfToInstance(intent)
        }
    }

    fun addFavorite(callback: (result: Boolean) -> Unit) {
        screenModelScope.launch {
            profile?.let {
                val result = FavoriteManager.addFavorite("friend", it.id, null, null)
                if (result)
                    FriendManager.setIsFavorite(it.id, true)
                callback(result)
            }
        }
    }

    fun removeFavorite(callback: (result: Boolean) -> Unit) {
        screenModelScope.launch {
            profile?.let {
                val result = FavoriteManager.removeFavorite("friend", it.id)
                if (result)
                    FriendManager.setIsFavorite(it.id, false)
                callback(result)
            }
        }
    }
}