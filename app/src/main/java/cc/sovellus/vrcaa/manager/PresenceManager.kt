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

package cc.sovellus.vrcaa.manager

import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.StatusHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PresenceManager : BaseManager<PresenceManager.PresenceListener>() {

    data class PresenceInfo(
        var worldName: String = "",
        var worldThumbnailUrl: String = "",
        var worldId: String = "",
        var instanceInfo: String = "",
        var instanceNonce: String = "",
        var instanceType: String = "",
        var userStatus: StatusHelper.Status = StatusHelper.Status.Offline
    )

    private val presenceLock = Any()
    private var presence: PresenceInfo = PresenceInfo()
    private val presenceStateFlow = MutableStateFlow(PresenceInfo())

    val presenceState: StateFlow<PresenceInfo> = presenceStateFlow.asStateFlow()

    interface PresenceListener {
        suspend fun onUpdatePresence(presence: PresenceInfo)
    }

    suspend fun updateWorld(info: PresenceInfo) {
        presence = info
        publishPresence()
    }

    suspend fun updateStatus(status: String) {
        presence = presence.apply {
            userStatus = StatusHelper.getStatusFromString(status)
        }
        publishPresence()
    }

    private suspend fun publishPresence() {
        synchronized(presenceLock) {
            presenceStateFlow.value = presence
        }
        val snapshot = presenceStateFlow.value
        getListeners().forEach { it.onUpdatePresence(snapshot) }
    }
}