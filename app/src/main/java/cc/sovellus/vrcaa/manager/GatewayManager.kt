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

import cc.sovellus.vrcaa.api.discord.GatewaySocket
import cc.sovellus.vrcaa.base.BaseManager

object GatewayManager : BaseManager<GatewayManager.GatewayListener>() {

    interface GatewayListener {
        suspend fun onUpdateWorld(info: GatewaySocket.PresenceInfo)
        suspend fun onUpdateStatus(status: String)
    }

    suspend fun updateWorld(info: GatewaySocket.PresenceInfo) {
        getListeners().forEach { listener ->
            listener.onUpdateWorld(info)
        }
    }

    suspend fun updateStatus(status: String) {
        getListeners().forEach { listener ->
            listener.onUpdateStatus(status)
        }
    }
}