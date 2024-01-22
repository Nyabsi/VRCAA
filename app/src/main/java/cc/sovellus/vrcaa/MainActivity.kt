package cc.sovellus.vrcaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import cafe.adriel.voyager.transitions.FadeTransition
import cc.sovellus.vrcaa.ui.screen.login.LoginScreen
import cc.sovellus.vrcaa.ui.screen.main.MainScreen
import cc.sovellus.vrcaa.ui.theme.Theme

class MainActivity : ComponentActivity() {
    private fun checkForCookies(): Boolean {
        return getSharedPreferences("vrcaa_prefs", 0).getString("cookies", "").isNullOrEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }
    }

    @Composable
    fun Content() {
        if (checkForCookies()) {

            Navigator(LoginScreen(), onBackPressed = { false }) { navigator -> SlideTransition(navigator)  }
        } else {
            Navigator(MainScreen(), onBackPressed = { it.key != "main" }) { navigator -> FadeTransition(navigator) }
        }
    }
}