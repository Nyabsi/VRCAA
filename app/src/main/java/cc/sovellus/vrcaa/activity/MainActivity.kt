package cc.sovellus.vrcaa.activity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.SlideTransition
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import cc.sovellus.vrcaa.ui.screen.login.LoginScreen
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen
import cc.sovellus.vrcaa.ui.theme.Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

        val invalidSession = intent.extras?.getBoolean("INVALID_SESSION")
        val terminateSession = intent.extras?.getBoolean("TERMINATE_SESSION")

        if (invalidSession == true) {
            Toast.makeText(
                this,
                getString(R.string.api_session_has_expired_text),
                Toast.LENGTH_LONG
            ).show()
        }

        val preferences = getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
        val token = preferences.authToken
        val twoFactorToken = preferences.twoFactorToken

        api.setToken(token)

        if (token.isNotBlank() && invalidSession == null) {
            val intent = Intent(this, PipelineService::class.java)
            startService(intent)
        }

        setContent {
            Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content(invalidSession == null && terminateSession == null && token.isNotBlank() && twoFactorToken.isNotBlank())
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