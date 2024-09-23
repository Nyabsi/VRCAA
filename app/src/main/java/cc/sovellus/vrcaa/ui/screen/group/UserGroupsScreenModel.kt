package cc.sovellus.vrcaa.ui.screen.group

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.UserGroups
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

sealed class UserGroupsState {
    data object Init : UserGroupsState()
    data object Loading : UserGroupsState()
    data class Result(val groups: List<UserGroups.Group>?) : UserGroupsState()
}

class UserGroupsScreenModel(
    private val userId: String
) : StateScreenModel<UserGroupsState>(UserGroupsState.Init) {

    private var groups: List<UserGroups.Group>? = null

    init {
        fetchGroups()
    }

    private fun fetchGroups() {
        mutableState.value = UserGroupsState.Loading
        screenModelScope.launch {
            App.setLoadingText(R.string.loading_text_groups)
            val groupsTemp = api.getUserGroups(userId).sortedBy { it.ownerId != userId }.toList()
            groups = groupsTemp
            mutableState.value = UserGroupsState.Result(groups)
        }
    }
}