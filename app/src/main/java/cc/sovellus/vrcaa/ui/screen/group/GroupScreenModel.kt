/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.sovellus.vrcaa.ui.screen.group

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.Group
import cc.sovellus.vrcaa.api.vrchat.http.models.GroupInstance
import cc.sovellus.vrcaa.manager.ApiManager.api
import kotlinx.coroutines.launch

class GroupScreenModel(
    private val context: Context, private val groupId: String
) : StateScreenModel<GroupScreenModel.GroupState>(GroupState.Init) {

    sealed class GroupState {
        data object Init : GroupState()
        data object Loading : GroupState()
        data class Result(val group: Group?, val instances: ArrayList<GroupInstance>) : GroupState()
    }

    private var group: Group? = null
    private var instances: ArrayList<GroupInstance> = arrayListOf()

    var currentIndex = mutableIntStateOf(0)
    var clickedInstance = mutableStateOf("")

    init {
        mutableState.value = GroupState.Loading
        App.setLoadingText(R.string.loading_text_group)
        fetchGroup()
    }

    private fun fetchGroup() {
        screenModelScope.launch {
            group = api.groups.fetchGroupByGroupId(groupId)
            instances = api.instances.fetchGroupInstancesById(groupId)
            mutableState.value = GroupState.Result(group, instances)
        }
    }

    fun withdrawInvite() {
        screenModelScope.launch {
            if (api.groups.withdrawRequestByGroupId(groupId)) {
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
            if (api.groups.joinGroupByGroupId(groupId)) {
                Toast.makeText(
                    context, if (open) {
                        context.getString(R.string.group_page_toast_join_group)
                    } else {
                        context.getString(R.string.group_page_toast_invite_requested)
                    }, Toast.LENGTH_SHORT
                ).show()
                fetchGroup()
            } else {
                Toast.makeText(
                    context, if (open) {
                        context.getString(R.string.group_page_toast_join_group_fail)
                    } else {
                        context.getString(R.string.group_page_toast_invite_requested_fail)
                    }, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun leaveGroup() {
        screenModelScope.launch {
            if (api.groups.leaveGroupByGroupId(groupId)) {
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
            api.instances.selfInvite(clickedInstance.value)
        }
    }
}