package cc.sovellus.vrcaa.ui.screen.main

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.helper.isMyServiceRunning
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainScreenModel(
    context: Context
) : ScreenModel {

    init {
        // Load the service here, I believe it's better than loading at `MainActivity`
        // Because if session expires, it would not restart the service.
        if (!context.isMyServiceRunning(PipelineService::class.java)) {
            val intent = Intent(context, PipelineService::class.java)
            context.startForegroundService(intent)
        }
    }

    var isSearchActive = mutableStateOf(false)
    var searchText = mutableStateOf("")
    var tonalElevation = mutableStateOf(16.dp)
    var searchHistory = mutableListOf<String>()

    // Bad code below.
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

            // don't add empty search to the queue.
            if (searchText.value.isNotEmpty())
                searchHistory.add(searchText.value)
            clearSearchText()
        }
    }

    fun clearSearchText() {
        searchText.value = ""
    }
}