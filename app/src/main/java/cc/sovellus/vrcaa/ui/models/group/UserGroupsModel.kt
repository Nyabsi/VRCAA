package cc.sovellus.vrcaa.ui.models.group

import android.content.Context
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.models.UserGroups
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class UserGroupsModel(
    private val context: Context,
    private val userId: String
) : StateScreenModel<UserGroupsModel.UserGroupsState>(UserGroupsState.Init) {

    sealed class UserGroupsState {
        data object Init : UserGroupsState()
        data object Loading : UserGroupsState()
        data class Result(val groups: UserGroups?) : UserGroupsState()
    }

    private var groups: UserGroups? = null

    init {
        fetchGroups()
    }

    private fun fetchGroups() {
        mutableState.value = UserGroupsState.Loading
        screenModelScope.launch {
            groups = api?.getGroups(userId)
            mutableState.value = UserGroupsState.Result(groups)
        }
    }
}