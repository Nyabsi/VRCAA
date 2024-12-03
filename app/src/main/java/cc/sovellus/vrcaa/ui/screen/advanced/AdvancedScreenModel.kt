package cc.sovellus.vrcaa.ui.screen.advanced

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getSystemService
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.networkLogging

class AdvancedScreenModel : ScreenModel {

    private val context: Context = App.getContext()
    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)

    val networkLoggingMode = mutableStateOf(preferences.networkLogging)

    fun toggleLogging() {
        networkLoggingMode.value = !networkLoggingMode.value
        preferences.networkLogging = !preferences.networkLogging

        Toast.makeText(
            context,
            context.getString(R.string.developer_mode_toggle_toast), // TODO: rename translation string
            Toast.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("BatteryLife")
    fun disableBatteryOptimizations() {
        val manager = getSystemService(context, PowerManager::class.java)
        manager?.let { pm ->
            if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:${context.packageName}")
                )
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
}