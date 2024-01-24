package cc.sovellus.vrcaa.ui.screen.main

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.service.PipelineService
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainScreenModel(
    private val context: Context
) : ScreenModel {

    private val api: ApiContext = ApiContext(context)

    var isSearchActive = mutableStateOf(false)
    var searchText = mutableStateOf("")
    var tonalElevation = mutableStateOf(16.dp)
    var searchHistory = mutableListOf<String>()

    init {
        initService()
    }

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

    private fun initService() {
        screenModelScope.launch {
            // Start running PipelineService on background.
            val intent = Intent(context, PipelineService::class.java)
            intent.putExtra("access_token", api.getAuth())
            intent.putExtra("online_friends", Gson().toJson(api.getFriends()))
            context.startService(intent)
        }
    }
}