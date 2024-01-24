package cc.sovellus.vrcaa.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import cc.sovellus.vrcaa.activity.login.LoginActivity
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
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            LocalContext.current.startActivity(intent)
        } else {
            Navigator(MainScreen(), onBackPressed = { it.key != "main" }) { navigator -> SlideTransition(navigator) }
        }
    }
}