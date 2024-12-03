package cc.sovellus.vrcaa.ui.screen.advanced

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
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
}