package cc.sovellus.vrcaa.ui.screen.group

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.models.Group
import cc.sovellus.vrcaa.api.vrchat.models.GroupInstances
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

sealed class GroupState {
    data object Init : GroupState()
    data object Loading : GroupState()
    data class Result(val group: Group?, val instances: GroupInstances?) : GroupState()
}

class GroupScreenModel(
    private val context: Context,
    private val groupId: String
) : StateScreenModel<GroupState>(GroupState.Init) {
    private var group: Group? = null
    private var instances: GroupInstances? = null

    var currentIndex = mutableIntStateOf(0)
    var clickedInstance = mutableStateOf("")


    init {
        fetchGroup()
    }

    private fun fetchGroup() {
        mutableState.value = GroupState.Loading
        screenModelScope.launch {
            group = api.getGroup(groupId)
            instances = api.getGroupInstances(groupId)
            mutableState.value = GroupState.Result(group, instances)
        }
    }

    fun withdrawInvite() {
        screenModelScope.launch {
            if (api.withdrawGroupJoinRequest(groupId)) {
                Toast.makeText(
                    context,
                    context.getString(R.string.group_page_toast_invite_requested_cancel),
                    Toast.LENGTH_SHORT
                ).show()
                fetchGroup()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.group_page_toast_invite_requested_cancel_fail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun joinGroup(open: Boolean) {
        screenModelScope.launch {
            if (api.joinGroup(groupId)) {
                Toast.makeText(
                    context,
                    if (open) {
                        context.getString(R.string.group_page_toast_join_group)
                    } else {
                        context.getString(R.string.group_page_toast_invite_requested)
                    },
                    Toast.LENGTH_SHORT
                ).show()
                fetchGroup()
            } else {
                Toast.makeText(
                    context,
                    if (open) {
                        context.getString(R.string.group_page_toast_join_group_fail)
                    } else {
                        context.getString(R.string.group_page_toast_invite_requested_fail)
                    },
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun leaveGroup() {
        screenModelScope.launch {
            if (api.leaveGroup(groupId)) {
                Toast.makeText(
                    context,
                    context.getString(R.string.group_page_toast_leave_group),
                    Toast.LENGTH_SHORT
                ).show()
                fetchGroup()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.group_page_toast_leave_group_fail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun selfInvite() {
        screenModelScope.launch {
            api.inviteSelfToInstance(clickedInstance.value)
        }
    }
}