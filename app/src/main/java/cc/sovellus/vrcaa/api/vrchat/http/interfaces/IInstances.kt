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

package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.GroupInstance
import cc.sovellus.vrcaa.api.vrchat.http.models.Instance

interface IInstances {

    // Intent = <worldId>:<InstanceId>:<Nonce>

    enum class InstanceType {
        Public,
        FriendsPlus,
        Friends,
        InvitePlus,
        Invite;

        override fun toString(): String {
            return when (this) {
                Public -> "public"
                FriendsPlus -> "hidden"
                Friends -> "friends"
                InvitePlus -> "private"
                Invite -> "private"
            }
        }
    }

    enum class InstanceRegion {
        Europe,
        Japan,
        AmericaDefault,
        AmericaEast,
        AmericaSouth;

        override fun toString(): String {
            return when (this) {
                Europe -> "eu"
                Japan -> "jp"
                AmericaDefault -> "us"
                AmericaEast -> "use"
                AmericaSouth -> "usw"
            }
        }
    }

    suspend fun fetchInstance(intent: String): Instance?
    suspend fun selfInvite(intent: String): Boolean
    suspend fun fetchGroupInstancesById(groupId: String): ArrayList<GroupInstance>
    suspend fun createInstance(worldId: String, type: InstanceType, region: InstanceRegion, ownerId: String?, canRequestInvite: Boolean): Instance?
}