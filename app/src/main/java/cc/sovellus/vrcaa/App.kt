package cc.sovellus.vrcaa

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.tab.Tab
import cc.sovellus.vrcaa.activity.CrashActivity
import cc.sovellus.vrcaa.extension.FeedTab
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.extension.favoritesTab
import cc.sovellus.vrcaa.extension.friendsTab
import cc.sovellus.vrcaa.extension.homeTab
import cc.sovellus.vrcaa.extension.minimalistMode
import cc.sovellus.vrcaa.extension.networkLogging
import cc.sovellus.vrcaa.extension.userHome
import cc.sovellus.vrcaa.helper.NotificationHelper
import cc.sovellus.vrcaa.ui.tabs.FavoritesTab
import cc.sovellus.vrcaa.ui.tabs.FeedTab
import cc.sovellus.vrcaa.ui.tabs.FriendsTab
import cc.sovellus.vrcaa.ui.tabs.HomeTab
import cc.sovellus.vrcaa.ui.tabs.ProfileTab
import cc.sovellus.vrcaa.ui.tabs.SettingsTab

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
        NotificationHelper.createNotificationChannels(this)

        context = this
        preferences = getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)

        networkLogging.value = preferences.networkLogging
        minimalistModeEnabled.value = preferences.minimalistMode
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        private lateinit var preferences: SharedPreferences

        private var networkLogging: MutableState<Boolean> = mutableStateOf(false)
        private var minimalistModeEnabled: MutableState<Boolean> = mutableStateOf(false)

        private var loadingText: MutableState<String> = mutableStateOf("")

        fun getContext(): Context { return context }

        fun isNetworkLoggingEnabled(): Boolean { return networkLogging.value }
        fun isMinimalistModeEnabled(): Boolean { return minimalistModeEnabled.value }

        fun ShowHome(): Int { return preferences.homeTab }
        fun ShowFriends(): Int { return preferences.friendsTab }
        fun ShowFavorites(): Int { return preferences.favoritesTab }
        fun ShowFeed(): Int { return preferences.FeedTab }


        private val tabs =
            arrayListOf(HomeTab, FriendsTab, FavoritesTab, FeedTab, ProfileTab, SettingsTab)
        fun userHome(): Tab { return tabs[preferences.userHome] }
        fun FallbackHome() {
            preferences.userHome = 4
        }

        @Composable
        fun isAppInDarkTheme(): Boolean { return isSystemInDarkTheme() && preferences.currentThemeOption != 0 }

        fun getLoadingText(): MutableState<String> { return loadingText }
        fun setLoadingText(resourceId: Int) { loadingText.value = context.getString(resourceId) }
    }
}