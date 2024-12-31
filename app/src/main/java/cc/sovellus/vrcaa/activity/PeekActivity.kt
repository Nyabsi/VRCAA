package cc.sovellus.vrcaa.activity


import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.SlideTransition
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.FeedManager
import cc.sovellus.vrcaa.manager.ThemeManager
import cc.sovellus.vrcaa.service.PipelineService
import cc.sovellus.vrcaa.ui.screen.avatar.AvatarScreen
import cc.sovellus.vrcaa.ui.screen.group.GroupScreen
import cc.sovellus.vrcaa.ui.screen.login.LoginScreen
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen
import cc.sovellus.vrcaa.ui.screen.profile.UserProfileScreen
import cc.sovellus.vrcaa.ui.screen.world.WorldInfoScreen
import cc.sovellus.vrcaa.ui.theme.LocalTheme
import cc.sovellus.vrcaa.ui.theme.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PeekActivity : ComponentActivity() {

    private val currentTheme = mutableIntStateOf(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var path = intent.data?.path
        path = path?.substring(6)

        val data = path?.split("/")
        // Log.d("VRCAA", "User clicked type ${data?.get(0)} with id of ${data?.get(1)}")

        val preferences: SharedPreferences = getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
        currentTheme.intValue = preferences.currentThemeOption

        setContent {
            CompositionLocalProvider(LocalTheme provides currentTheme.intValue) {
                Theme(LocalTheme.current) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Content(type = data?.get(0) ?: "unknown", id = data?.get(1) ?: "")
                    }
                }
            }
        }
    }

    @Composable
    fun Content(type: String, id: String) {
        var screen: Screen? = null

        when (type) {
            "world" -> {
                screen = WorldInfoScreen(id, true)
            }
            "user" -> {
                screen = UserProfileScreen(id, true)
            }
            "avatar" -> {
                screen = AvatarScreen(id, true)
            }
            "group" -> {
                screen = GroupScreen(id, true)
            }
            else -> finish()
        }

        screen?.let {
            Navigator(
                screen = it,
                disposeBehavior = NavigatorDisposeBehavior(
                    disposeNestedNavigators = false,
                    disposeSteps = false
                ),
                onBackPressed = { true }
            ) { navigator ->
                SlideTransition(navigator = navigator)
            }
        }
    }
}