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
import androidx.core.database.getStringOrNull
import cc.sovellus.vrcaa.base.BaseManager
import cc.sovellus.vrcaa.helper.DatabaseHelper
import cc.sovellus.vrcaa.helper.StatusHelper
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

object DatabaseManager: BaseManager<Any>() {
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

    fun readFeeds(): MutableList<FeedManager.Feed> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.Tables.SQL_TABLE_FEED,
            arrayOf("type", "feedId", "friendId", "friendName", "friendPictureUrl", "friendStatus", "travelDestination", "worldId", "avatarName", "feedTimestamp"),
            null,
            null,
            null,
            null,
            null
        )

        val feeds = mutableListOf<FeedManager.Feed>()

        with(cursor) {
            while (moveToNext()) {
                val feed = FeedManager.Feed(
                    type = FeedManager.FeedType.fromInt(getInt(getColumnIndexOrThrow("type"))),
                    feedId = UUID.fromString(getString(getColumnIndexOrThrow("feedId"))),
                    friendId = getString(getColumnIndexOrThrow("friendId")),
                    friendName = getString(getColumnIndexOrThrow("friendName")),
                    friendPictureUrl = getString(getColumnIndexOrThrow("friendPictureUrl")),
                    friendStatus = StatusHelper.Status.fromInt(getInt(getColumnIndexOrThrow("friendStatus"))),
                    travelDestination = getString(getColumnIndexOrThrow("travelDestination")),
                    worldId = getString(getColumnIndexOrThrow("worldId")),
                    avatarName = getStringOrNull(getColumnIndex("avatarName")) ?: "",
                    feedTimestamp = LocalDateTime.ofEpochSecond(getLong(getColumnIndexOrThrow("feedTimestamp")), 0, ZoneOffset.UTC)
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
                queries.add(getString(getColumnIndexOrThrow("query")))
            }
        }

        cursor.close()
        return queries
    }
}