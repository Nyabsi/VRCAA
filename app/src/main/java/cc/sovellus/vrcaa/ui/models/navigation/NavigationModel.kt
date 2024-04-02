package cc.sovellus.vrcaa.ui.models.navigation

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.api.vrchat.http.models.LimitedUser
import cc.sovellus.vrcaa.api.updater.AutoUpdater
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FriendManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(DelicateCoroutinesApi::class)
class NavigationScreenModel : ScreenModel {

    var isSearchActive = mutableStateOf(false)
    var searchText = mutableStateOf("")
    var tonalElevation = mutableStateOf(16.dp)
    var searchHistory = mutableListOf<String>()

    var hasUpdate = mutableStateOf(false)

    init {
        screenModelScope.launch {
            hasUpdate.value = AutoUpdater.checkForUpdates()

            val friends: MutableList<LimitedUser> = ArrayList()
            api.getFriends()?.let { friends += it }
            api.getFriends(true)?.let { friends += it }
            FriendManager.setFriends(friends)
        }
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
            if (AutoUpdater.downloadUpdate(update)) {
                AutoUpdater.installUpdate(context, update)
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