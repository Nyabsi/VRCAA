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

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.DatabaseHelper
import cc.sovellus.vrcaa.helper.StatusHelper
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

object DatabaseManager: BaseManager<Any>() {

    data class LocationHistory(
        val worldId: String,
        val worldName: String,
        val tags: List<String>,
        val authorId: String,
        val authorName: String,
        val thumbnailImageUrl: String,
        val heat: Int,
        val popularity: Int,
        val favorites: Int,
        val visits: Int,
        val capacity: Int,
        val recommendedCapacity: Int,
        val releaseStatus: String,
        val publicationDate: String,
        val timeSpent: Long,
        val lastVisited: LocalDateTime
    )

    private val gson by lazy { Gson() }
    val db = DatabaseHelper()

    fun writeFeed(feed: FeedManager.Feed) {
        val values = ContentValues().apply {
            put("type", feed.type.ordinal)
            put("feedId", feed.feedId.toString())
            put("friendId", feed.friendId)
            put("friendName", feed.friendName)
            put("friendPictureUrl", feed.friendPictureUrl)
            put("friendStatus", feed.friendStatus.ordinal)
            put("travelDestination", feed.travelDestination)
            put("worldId", feed.worldId)
            put("avatarName", feed.avatarName)
            put("feedTimestamp", feed.feedTimestamp.toEpochSecond(ZoneOffset.UTC))
        }

        db.writableDatabase.insert(DatabaseHelper.Tables.SQL_TABLE_FEED, null, values)
    }

    fun readFeeds(limit: Int = 1000): MutableList<FeedManager.Feed> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.Tables.SQL_TABLE_FEED,
            arrayOf("type", "feedId", "friendId", "friendName", "friendPictureUrl", "friendStatus", "travelDestination", "worldId", "avatarName", "feedTimestamp"),
            null,
            null,
            null,
            null,
            "feedTimestamp DESC",
            limit.toString()
        )

        val feeds = mutableListOf<FeedManager.Feed>()

        with(cursor) {
            while (moveToNext()) {
                val feed = FeedManager.Feed(
                    type = FeedManager.FeedType.fromInt(getInt(getColumnIndexOrThrow("type"))),
                    feedId = getStringOrNull(getColumnIndex("feedId"))?.let { UUID.fromString(it) } ?: UUID.randomUUID(),
                    friendId = getStringOrNull(getColumnIndex("friendId")) ?: "",
                    friendName = getStringOrNull(getColumnIndex("friendName")) ?: "",
                    friendPictureUrl = getStringOrNull(getColumnIndex("friendPictureUrl")) ?: "",
                    friendStatus = StatusHelper.Status.fromInt(getInt(getColumnIndexOrThrow("friendStatus"))),
                    travelDestination = getStringOrNull(getColumnIndex("travelDestination")) ?: "",
                    worldId = getStringOrNull(getColumnIndex("worldId")) ?: "",
                    avatarName = getStringOrNull(getColumnIndex("avatarName")) ?: "",
                    feedTimestamp = getLongOrNull(getColumnIndex("feedTimestamp"))
                        ?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
                        ?: LocalDateTime.now(ZoneOffset.UTC)
                )
                feeds.add(feed)
            }
        }

        cursor.close()
        return feeds
    }

    fun writeQuery(query: String) {
        val values = ContentValues().apply {
            put("query", query)
        }

        db.writableDatabase.insert(DatabaseHelper.Tables.SQL_TABLE_SEARCH_HISTORY, null, values)
    }

    fun readQueries(): MutableList<String> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.Tables.SQL_TABLE_SEARCH_HISTORY, arrayOf("query"), null, null, null, null, null
        )

        val queries = mutableListOf<String>()

        with(cursor) {
            while (moveToNext()) {
                val query = getStringOrNull(getColumnIndex("query")) ?: continue
                queries.add(query)
            }
        }

        cursor.close()
        return queries
    }

    fun writeLocation(location: LocationHistory) {
        val existing = readLocationByWorldId(location.worldId)
        val accumulatedTimeSpent = (existing?.timeSpent ?: 0L) + location.timeSpent

        val values = ContentValues().apply {
            put("worldId", location.worldId)
            put("worldName", location.worldName)
            put("tags", gson.toJson(location.tags))
            put("authorId", location.authorId)
            put("authorName", location.authorName)
            put("thumbnailImageUrl", location.thumbnailImageUrl)
            put("heat", location.heat)
            put("popularity", location.popularity)
            put("favorites", location.favorites)
            put("visits", location.visits)
            put("capacity", location.capacity)
            put("recommendedCapacity", location.recommendedCapacity)
            put("releaseStatus", location.releaseStatus)
            put("publicationDate", location.publicationDate)
            put("timeSpent", accumulatedTimeSpent)
            put("lastVisited", location.lastVisited.toEpochSecond(ZoneOffset.UTC))
        }

        db.writableDatabase.insertWithOnConflict(
            DatabaseHelper.Tables.SQL_TABLE_LOCATION_HISTORY,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    private fun queryLocations(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        orderBy: String? = "timeSpent DESC",
        limit: String? = null
    ): MutableList<LocationHistory> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.Tables.SQL_TABLE_LOCATION_HISTORY,
            arrayOf("worldId", "worldName", "tags", "authorId", "authorName", "thumbnailImageUrl",
                "heat", "popularity", "favorites", "visits", "capacity", "recommendedCapacity",
                "releaseStatus", "publicationDate", "timeSpent", "lastVisited"),
            selection,
            selectionArgs,
            null,
            null,
            orderBy,
            limit
        )

        val locations = mutableListOf<LocationHistory>()

        with(cursor) {
            while (moveToNext()) {
                val tagsJson = getStringOrNull(getColumnIndex("tags")) ?: "[]"
                val tags = runCatching {
                    gson.fromJson(tagsJson, Array<String>::class.java).toList()
                }.getOrDefault(emptyList())

                val location = LocationHistory(
                    worldId = getStringOrNull(getColumnIndex("worldId")) ?: "",
                    worldName = getStringOrNull(getColumnIndex("worldName")) ?: "",
                    tags = tags,
                    authorId = getStringOrNull(getColumnIndex("authorId")) ?: "",
                    authorName = getStringOrNull(getColumnIndex("authorName")) ?: "",
                    thumbnailImageUrl = getStringOrNull(getColumnIndex("thumbnailImageUrl")) ?: "",
                    heat = getInt(getColumnIndexOrThrow("heat")),
                    popularity = getInt(getColumnIndexOrThrow("popularity")),
                    favorites = getInt(getColumnIndexOrThrow("favorites")),
                    visits = getInt(getColumnIndexOrThrow("visits")),
                    capacity = getInt(getColumnIndexOrThrow("capacity")),
                    recommendedCapacity = getInt(getColumnIndexOrThrow("recommendedCapacity")),
                    releaseStatus = getStringOrNull(getColumnIndex("releaseStatus")) ?: "",
                    publicationDate = getStringOrNull(getColumnIndex("publicationDate")) ?: "",
                    timeSpent = getLongOrNull(getColumnIndex("timeSpent")) ?: 0L,
                    lastVisited = getLongOrNull(getColumnIndex("lastVisited"))
                        ?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
                        ?: LocalDateTime.now(ZoneOffset.UTC)
                )
                locations.add(location)
            }
        }

        cursor.close()
        return locations
    }

    fun readLocations(limit: Int = 100): MutableList<LocationHistory> {
        return queryLocations(limit = limit.toString())
    }

    fun readTopLocations(limit: Int = 10): MutableList<LocationHistory> {
        return queryLocations(orderBy = "timeSpent DESC", limit = limit.toString())
    }

    fun readBouncedLocations(maxTimeSpent: Long = 60, limit: Int = 50): MutableList<LocationHistory> {
        return queryLocations(
            selection = "timeSpent < ?",
            selectionArgs = arrayOf(maxTimeSpent.toString()),
            orderBy = "timeSpent ASC",
            limit = limit.toString()
        )
    }

    fun readLocationByWorldId(worldId: String): LocationHistory? {
        return queryLocations(
            selection = "worldId = ?",
            selectionArgs = arrayOf(worldId),
            limit = "1"
        ).firstOrNull()
    }

    fun readVisitedWorldIds(): Set<String> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.Tables.SQL_TABLE_LOCATION_HISTORY,
            arrayOf("worldId"), null, null, null, null, null
        )

        val ids = mutableSetOf<String>()

        with(cursor) {
            while (moveToNext()) {
                getStringOrNull(getColumnIndex("worldId"))?.let { ids.add(it) }
            }
        }

        cursor.close()
        return ids
    }

    fun deleteLocation(worldId: String) {
        db.writableDatabase.delete(
            DatabaseHelper.Tables.SQL_TABLE_LOCATION_HISTORY,
            "worldId = ?",
            arrayOf(worldId)
        )
    }

    fun clearLocationHistory() {
        db.writableDatabase.delete(
            DatabaseHelper.Tables.SQL_TABLE_LOCATION_HISTORY,
            null,
            null
        )
    }
}