package cc.sovellus.vrcaa.ui.screen.group

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.models.UserGroups
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

sealed class UserGroupsState {
    data object Init : UserGroupsState()
    data object Loading : UserGroupsState()
    data class Result(val groups: ArrayList<UserGroups.Group>?) : UserGroupsState()
}

class UserGroupsScreenModel(
    private val userId: String
) : StateScreenModel<UserGroupsState>(UserGroupsState.Init) {

    private var groups: ArrayList<UserGroups.Group>? = null

    init {
        fetchGroups()
    }

    private fun fetchGroups() {
        mutableState.value = UserGroupsState.Loading
        screenModelScope.launch {
            groups = api.getUserGroups(userId)
            mutableState.value = UserGroupsState.Result(groups)
        }
    }
}