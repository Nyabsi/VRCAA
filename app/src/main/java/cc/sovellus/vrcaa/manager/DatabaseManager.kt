package cc.sovellus.vrcaa.manager

import android.content.ContentValues
import cc.sovellus.vrcaa.helper.DatabaseHelper
import cc.sovellus.vrcaa.helper.StatusHelper
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

object DatabaseManager {
    private val dbHelper = DatabaseHelper()

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
            put("feedTimestamp", feed.feedTimestamp.toEpochSecond(ZoneOffset.UTC))
        }

        dbHelper.writableDatabase.insert(DatabaseHelper.Tables.SQL_TABLE_FEED, null, values)
    }

    fun readFeeds(): MutableList<FeedManager.Feed> {
        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.Tables.SQL_TABLE_FEED,
            arrayOf("type", "feedId", "friendId", "friendName", "friendPictureUrl", "friendStatus", "travelDestination", "worldId", "feedTimestamp"),
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
                    feedTimestamp = LocalDateTime.ofEpochSecond(getLong(getColumnIndexOrThrow("feedTimestamp")), 0, ZoneOffset.UTC)
                )
                feeds.add(feed)
            }
        }

        cursor.close()
        return feeds
    }
}