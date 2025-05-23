package cc.sovellus.vrcaa.ui.screen.about

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.crashAnalytics

class AboutScreenModel : ScreenModel {
    private val context: Context = App.getContext()
    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
    val crashAnalytics = mutableStateOf(preferences.crashAnalytics)

    fun toggleAnalytics() {
        preferences.crashAnalytics = !preferences.crashAnalytics
        crashAnalytics.value = !crashAnalytics.value

        Toast.makeText(
            context,
            context.getString(R.string.developer_mode_toggle_toast),
            Toast.LENGTH_SHORT
        ).show()
    }
}