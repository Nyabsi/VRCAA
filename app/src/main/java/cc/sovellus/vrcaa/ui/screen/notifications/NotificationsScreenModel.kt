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

package cc.sovellus.vrcaa.ui.screen.notifications

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.api.vrchat.http.models.Notification
import cc.sovellus.vrcaa.api.vrchat.http.models.NotificationV2
import cc.sovellus.vrcaa.api.vrchat.http.models.Notifications
import cc.sovellus.vrcaa.api.vrchat.http.models.NotificationsV2
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.NotificationManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationsScreenModel : StateScreenModel<NotificationsScreenModel.NotificationsState>(NotificationsState.Init) {

    sealed class NotificationsState {
        data object Init : NotificationsState()
        data object Loading : NotificationsState()
        data object Loaded : NotificationsState()
    }

    private var notificationStateFlow = MutableStateFlow(listOf<Notification>())
    var notifications = notificationStateFlow.asStateFlow()

    private var notificationV2StateFlow = MutableStateFlow(listOf<NotificationV2>())
    var notificationsV2 = notificationV2StateFlow.asStateFlow()

    var users: List<LimitedUser?> = arrayListOf<LimitedUser?>()

    var currentIndex = mutableIntStateOf(0)
    var currentNotification = mutableStateOf<Notification?>(null)
    var currentNotificationV2 = mutableStateOf<NotificationV2?>(null)

    private val listener = object : NotificationManager.NotificationListener {
        override fun onUpdateNotifications(notifications: List<Notification>) {
            mutableState.value = NotificationsState.Loading
            notificationStateFlow.value = notifications
            fetchUsersFromNotifications()
        }

        override fun onUpdateNotificationsV2(notifications: List<NotificationV2>) {
            mutableState.value = NotificationsState.Loading
            notificationV2StateFlow.value = notifications
            mutableState.value = NotificationsState.Loaded
        }

        override fun onUpdateNotificationCount(count: Int) { }
    }

    fun fetchUsersFromNotifications() {
        screenModelScope.launch {
            users += notificationStateFlow.value.filter { it.senderUserId.isNotEmpty() && users.find { user -> user?.id == it.senderUserId }  == null }.map {
                async {
                    api.users.fetchUserByUserId(it.senderUserId)
                }
            }.awaitAll()
            mutableState.value = NotificationsState.Loaded
        }
    }

    init {
        mutableState.value = NotificationsState.Loading
        App.setLoadingText(R.string.loading_text_notifications)
        NotificationManager.addListener(listener)
        notificationStateFlow.update { NotificationManager.getNotifications() }
        notificationV2StateFlow.update { NotificationManager.getNotificationsV2() }
        fetchUsersFromNotifications()
    }
}