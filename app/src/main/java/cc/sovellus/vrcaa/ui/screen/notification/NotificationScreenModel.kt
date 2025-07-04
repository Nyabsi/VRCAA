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

package cc.sovellus.vrcaa.ui.screen.notification

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.helper.NotificationHelper

class NotificationScreenModel(private val friendId: String) : ScreenModel {

    val isNotificationsEnabled = mutableStateOf(NotificationHelper.isOnWhitelist(friendId))

    val isOnlineIntentEnabled = mutableStateOf(
        NotificationHelper.isIntentEnabled(
            friendId, NotificationHelper.Intents.FRIEND_FLAG_ONLINE
        )
    )
    val isOfflineIntentEnabled = mutableStateOf(
        NotificationHelper.isIntentEnabled(
            friendId, NotificationHelper.Intents.FRIEND_FLAG_OFFLINE
        )
    )
    val isLocationIntentEnabled = mutableStateOf(
        NotificationHelper.isIntentEnabled(
            friendId, NotificationHelper.Intents.FRIEND_FLAG_LOCATION
        )
    )
    val isStatusIntentEnabled = mutableStateOf(
        NotificationHelper.isIntentEnabled(
            friendId, NotificationHelper.Intents.FRIEND_FLAG_STATUS
        )
    )


    fun toggleNotifications(toggle: Boolean) {

        if (toggle) {
            NotificationHelper.addToWhitelist(friendId)
            isNotificationsEnabled.value = true
        } else {
            NotificationHelper.removeFromWhiteList(friendId)
            isNotificationsEnabled.value = false
        }
    }

    fun toggleIntent(toggle: Boolean, intent: NotificationHelper.Intents) {

        if (toggle) {
            NotificationHelper.enableIntent(friendId, intent)
        } else {
            NotificationHelper.disableIntent(friendId, intent)
        }

        when (intent) {
            NotificationHelper.Intents.FRIEND_FLAG_ONLINE -> isOnlineIntentEnabled.value = toggle
            NotificationHelper.Intents.FRIEND_FLAG_OFFLINE -> isOfflineIntentEnabled.value = toggle
            NotificationHelper.Intents.FRIEND_FLAG_LOCATION -> isLocationIntentEnabled.value =
                toggle

            NotificationHelper.Intents.FRIEND_FLAG_STATUS -> isStatusIntentEnabled.value = toggle
        }
    }
}