package cc.sovellus.vrcaa.ui.screen.group

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.UserGroup
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class UserGroupsScreenModel(
    private val userId: String
) : StateScreenModel<UserGroupsScreenModel.UserGroupsState>(UserGroupsState.Init) {

    sealed class UserGroupsState {
        data object Init : UserGroupsState()
        data object Loading : UserGroupsState()
        data object Empty : UserGroupsState()
        data class Result(val groups: List<UserGroup>) : UserGroupsState()
    }

    init {
        mutableState.value = UserGroupsState.Loading
        fetchGroups()
    }

    private fun fetchGroups() {
        screenModelScope.launch {
            App.setLoadingText(R.string.loading_text_groups)
            val result = api.users.fetchGroupsByUserId(userId).sortedBy { it.ownerId != userId }
            if (result.isEmpty())
                mutableState.value = UserGroupsState.Empty
            else
                mutableState.value = UserGroupsState.Result(result)
        }
    }
}