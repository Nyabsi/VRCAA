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

import cc.sovellus.vrcaa.api.vrchat.http.models.Avatar
import cc.sovellus.vrcaa.api.vrchat.http.models.Notification
import cc.sovellus.vrcaa.api.vrchat.http.models.User

interface IUser {

    // V1 Notification API aka "User Notification"
    suspend fun markNotificationAsRead(notificationId: String): Notification?
    suspend fun hideNotification(notificationId: String): Notification?
    suspend fun fetchNotifications(n: Int = 100, offset: Int = 0, notifications: ArrayList<Notification> = arrayListOf()): ArrayList<Notification>

    suspend fun updateProfileByUserId(userId: String, newStatus: String, newDescription: String, newBio: String, newBioLinks: List<String>, newPronouns: String, newAgeVerificationStatus: String?): User?
    suspend fun fetchOwnedAvatars(n: Int = 100, offset: Int = 0, avatars: ArrayList<Avatar> = arrayListOf()): ArrayList<Avatar>
}