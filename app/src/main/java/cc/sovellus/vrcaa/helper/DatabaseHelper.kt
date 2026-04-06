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

package cc.sovellus.vrcaa.helper

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cc.sovellus.vrcaa.App

class DatabaseHelper : SQLiteOpenHelper(App.getContext(),
    Constants.DATABASE_NAME, null,
    Constants.DATABASE_VERSION
) {
    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(Queries.SQL_CREATE_FEED_TABLE)
        database.execSQL(Queries.SQL_CREATE_SEARCH_HISTORY_TABLE)
        database.execSQL(Queries.SQL_CREATE_FEED_TIMESTAMP_INDEX)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        if (oldVersion <= 1 && newVersion >= 2) {
            database.execSQL(Migrations.SQL_FEED_TABLE_V2_MIGRATION)
        }

        if (oldVersion <= 2 && newVersion >= 3) {
            database.execSQL(Queries.SQL_CREATE_SEARCH_HISTORY_TABLE)
        }

        if (oldVersion <= 3 && newVersion >= 4) {
            database.execSQL(Queries.SQL_CREATE_FEED_TIMESTAMP_INDEX)
        }
    }

    override fun onDowngrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        if (newVersion < 4) {
            database.execSQL(Downgrades.SQL_DROP_FEED_TIMESTAMP_INDEX)
        }
    }

    object Constants {
        const val DATABASE_NAME = "vrcaa.db"
        const val DATABASE_VERSION = 4
    }

    object Queries {
        const val SQL_CREATE_FEED_TABLE = "CREATE TABLE feed(type INTEGER, feedId TEXT, friendId TEXT, friendName TEXT, friendPictureUrl TEXT, friendStatus TEXT, travelDestination TEXT, worldId TEXT, avatarName TEXT, feedTimestamp BIGINT)"
        const val SQL_CREATE_SEARCH_HISTORY_TABLE = "CREATE TABLE search_history(query TEXT)"
        const val SQL_CREATE_FEED_TIMESTAMP_INDEX = "CREATE INDEX IF NOT EXISTS idx_feed_timestamp ON feed(feedTimestamp DESC)"
    }

    object Migrations {
        const val SQL_FEED_TABLE_V2_MIGRATION = "ALTER TABLE feed ADD avatarName TEXT"
    }

    object Downgrades {
        const val SQL_DROP_FEED_TIMESTAMP_INDEX = "DROP INDEX IF EXISTS idx_feed_timestamp"
    }

    object Tables {
        const val SQL_TABLE_FEED = "feed"
        const val SQL_TABLE_SEARCH_HISTORY = "search_history"
    }
}