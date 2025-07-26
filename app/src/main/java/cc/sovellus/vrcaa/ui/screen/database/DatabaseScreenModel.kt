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

package cc.sovellus.vrcaa.ui.screen.database

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.widget.Toast
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.manager.DatabaseManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.format
import java.io.File

class DatabaseScreenModel : ScreenModel {

    private val context: Context = App.getContext()

    fun getDatabaseSizeReadable(): String {
        val file = File(context.getDatabasePath("vrcaa.db").path)
        return format("%.2f MB", file.length().toDouble() / 1_000_000)
    }

    fun getDatabaseRowsReadable(): String {
        val db = DatabaseManager.db.readableDatabase
        var totalRows = 0

        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'",
            null
        )

        while (cursor.moveToNext()) {
            val tableName = cursor.getString(0)
            val countCursor = db.rawQuery("SELECT COUNT(*) FROM $tableName", null)
            if (countCursor.moveToFirst()) {
                totalRows += countCursor.getInt(0)
            }
            countCursor.close()
        }

        cursor.close()
        return "$totalRows rows"
    }

    fun getGlideCacheSizeReadable(): String {
        val cacheDir = File(context.cacheDir, "image_manager_disk_cache")
        var size: Long = 0
        if (cacheDir.exists()) {
            cacheDir.listFiles()?.forEach { file ->
                size += file.length()
            }
        }
        return format("%.2f MB", size.toDouble() / 1_000_000)
    }

    fun cleanGlideCache() {
        screenModelScope.launch(Dispatchers.IO) {
            Glide.get(context).clearDiskCache()
        }
        Toast.makeText(
            context,
            context.getString(R.string.database_page_glide_clean_cache_toast),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun backupDatabaseToUri(uri: Uri) {
        val file = File(context.getDatabasePath("vrcaa.db").path)
        App.getContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
            file.inputStream().copyTo(outputStream)
        }
    }

    fun restoreDatabaseFromUri(uri: Uri) {
        val tempFile = File.createTempFile("restore", ".db", context.cacheDir)

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        val isValid = try {
            val tempDb = SQLiteDatabase.openDatabase(
                tempFile.path,
                null,
                SQLiteDatabase.OPEN_READONLY
            )

            val cursor = tempDb.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
            val hasTables = cursor.use { it.count > 0 }
            tempDb.close()

            hasTables
        } catch (_: Throwable) {
            false
        }

        if (!isValid) {
            tempFile.delete()
            Toast.makeText(
                context,
                context.getString(R.string.database_page_toast_recovery_failed),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        DatabaseManager.db.close()
        val databaseFile = File(context.getDatabasePath("vrcaa.db").path)
        databaseFile.delete()
        tempFile.copyTo(databaseFile, overwrite = true)

        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(context.packageName)
        val mainIntent = Intent.makeRestartActivityTask(intent?.component)
        context.startActivity(mainIntent)

        Runtime.getRuntime().exit(0)
    }
}
