package cc.sovellus.vrcaa.ui.screen.main

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.updater.AutoUpdater
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainScreenModel : ScreenModel {

    var isSearchActive = mutableStateOf(false)
    var searchText = mutableStateOf("")
    var tonalElevation = mutableStateOf(16.dp)
    var searchHistory = mutableListOf<String>()

    private val updater = AutoUpdater()
    var hasUpdate = mutableStateOf(false)

    init {
        hasUpdate.value = updater.checkForUpdates()
    }

    fun enterSearchMode() {
        screenModelScope.launch {
            tonalElevation.value = 0.dp
            delay(100)
            isSearchActive.value = true
        }
    }

    fun existSearchMode() {
        screenModelScope.launch {
            isSearchActive.value = false
            delay(160)
            tonalElevation.value = 16.dp

            if (searchText.value.isNotEmpty())
                searchHistory.add(searchText.value)
            clearSearchText()
        }
    }

    fun clearSearchText() {
        searchText.value = ""
    }

    fun update(context: Context) {
        val update = File(context.filesDir, "temp.apk")
        screenModelScope.launch {
            if (updater.downloadUpdate(update)) {
                updater.installUpdate(context, update)
            } else {
                Toast.makeText(
                    context,
                    "Failed to download update...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}