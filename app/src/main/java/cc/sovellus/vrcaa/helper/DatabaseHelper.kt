package cc.sovellus.vrcaa.helper

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.helper.DatabaseHelper.Queries.SQL_FEED_TABLE_V2_MIGRATION

class DatabaseHelper : SQLiteOpenHelper(App.getContext(),
    Constants.DATABASE_NAME, null,
    Constants.DATABASE_VERSION
) {
    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(Queries.SQL_CREATE_FEED_TABLE)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        if (oldVersion <= 1 && newVersion >= 2) {
            database.execSQL(SQL_FEED_TABLE_V2_MIGRATION)
        }

        // onUpgrade(database, oldVersion, newVersion)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onDowngrade(db, oldVersion, newVersion)
    }

    object Constants {
        const val DATABASE_NAME = "vrcaa.db"
        const val DATABASE_VERSION = 2
    }

    object Queries {
        const val SQL_CREATE_FEED_TABLE = "CREATE TABLE feed(type INTEGER, feedId TEXT, friendId TEXT, friendName TEXT, friendPictureUrl TEXT, friendStatus TEXT, travelDestination TEXT, worldId TEXT, avatarName TEXT, feedTimestamp BIGINT)"
        const val SQL_FEED_TABLE_V2_MIGRATION = "ALTER TABLE feed ADD avatarName TEXT"
    }

    object Tables {
        const val SQL_TABLE_FEED = "feed"
    }
}