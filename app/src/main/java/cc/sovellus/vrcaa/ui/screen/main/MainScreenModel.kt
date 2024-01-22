package cc.sovellus.vrcaa.ui.screen.main

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainScreenModel : ScreenModel {

    var isSearchActive = mutableStateOf(false)
    var searchText = mutableStateOf("")
    var tonalElevation = mutableStateOf(16.dp)
    var searchHistory = mutableListOf<String>()

    // HACK: those fucking shit below, it's the only way this shit works. I don't know why the fuck
    // Google designed the SearchBar component to be utter shit.
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