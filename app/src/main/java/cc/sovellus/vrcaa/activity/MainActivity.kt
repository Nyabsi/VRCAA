package cc.sovellus.vrcaa.activity


import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import cc.sovellus.vrcaa.ui.screen.login.LoginScreen
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen
import cc.sovellus.vrcaa.ui.theme.LocalTheme
import cc.sovellus.vrcaa.ui.theme.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {

    val currentTheme = mutableIntStateOf(-1)

    private val themeListener = object : ThemeManager.ThemeListener {
        override fun onPreferenceUpdate(theme: Int) {
            currentTheme.intValue = theme
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // TODO: make a "initial setup" screen and throw this there.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }

        ThemeManager.addThemeListener(themeListener)

        val preferences: SharedPreferences = getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
        currentTheme.intValue = preferences.currentThemeOption

        val invalidSession = intent.extras?.getBoolean("INVALID_SESSION")
        val terminateSession = intent.extras?.getBoolean("TERMINATE_SESSION")
        val restartSession = intent.extras?.getBoolean("RESTART_SESSION")

        if (invalidSession == true) {

            preferences.authToken = ""

            val intent = Intent(this, PipelineService::class.java)
            stopService(intent)

            Toast.makeText(
                this,
                getString(R.string.api_session_has_expired_text),
                Toast.LENGTH_LONG
            ).show()
        }

        if (restartSession == true) {
            val intent = Intent(this, PipelineService::class.java)
            stopService(intent)
            startService(intent)
        }

        val token = preferences.authToken
        if (token.isNotBlank() && invalidSession == null && terminateSession == null && restartSession == null) {
            val intent = Intent(this, PipelineService::class.java)
            startService(intent)
        }

        setContent {
            CompositionLocalProvider(LocalTheme provides currentTheme.intValue) {
                Theme(LocalTheme.current) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Content(token.isNotBlank() && invalidSession == null && terminateSession == null)
                    }
                }
            }
        }
    }

    @Composable
    fun Content(authenticated: Boolean) {

        Navigator(
            screen = if (authenticated) {
                NavigationScreen()
            } else {
                LoginScreen()
            },
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