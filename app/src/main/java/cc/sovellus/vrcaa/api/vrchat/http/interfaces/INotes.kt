package cc.sovellus.vrcaa.api.vrchat.http.interfaces

import cc.sovellus.vrcaa.api.vrchat.http.models.Notification

interface INotes {
    suspend fun updateNote(userId: String, note: String): Notification?
}