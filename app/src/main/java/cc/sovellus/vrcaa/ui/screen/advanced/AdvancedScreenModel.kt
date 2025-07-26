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

package cc.sovellus.vrcaa.ui.screen.advanced

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.networkLogging
import cc.sovellus.vrcaa.service.PipelineService
import androidx.core.net.toUri
import cc.sovellus.vrcaa.manager.DatabaseManager
import java.io.File

class AdvancedScreenModel : ScreenModel {

    private val context: Context = App.getContext()
    private val preferences: SharedPreferences = App.getPreferences()

    val networkLoggingMode = mutableStateOf(preferences.networkLogging)

    fun toggleLogging() {
        networkLoggingMode.value = !networkLoggingMode.value
        preferences.networkLogging = !preferences.networkLogging
    }

    @SuppressLint("BatteryLife")
    fun disableBatteryOptimizations() {
        val manager = context.getSystemService(PowerManager::class.java)
        manager?.let { pm ->
            if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    "package:${context.packageName}".toUri()
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }      
                context.startActivity(intent)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.about_page_battery_optimizations_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun killBackgroundService() {
        val intent = Intent(context, PipelineService::class.java)
        context.stopService(intent)
    }

    fun deleteDatabase() {
        DatabaseManager.db.close()

        val file = File(context.getDatabasePath("vrcaa.db").path)
        file.delete()

        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage(context.packageName)
        val mainIntent = Intent.makeRestartActivityTask(intent?.component)
        context.startActivity(mainIntent)

        Runtime.getRuntime().exit(0)
    }
}
