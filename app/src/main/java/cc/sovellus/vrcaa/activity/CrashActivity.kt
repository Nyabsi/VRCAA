package cc.sovellus.vrcaa.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import cc.sovellus.vrcaa.GlobalExceptionHandler
import cc.sovellus.vrcaa.extension.currentThemeOption
import cc.sovellus.vrcaa.ui.screen.crash.CrashScreen
import cc.sovellus.vrcaa.ui.theme.LocalTheme
import cc.sovellus.vrcaa.ui.theme.Theme

class CrashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val exception = GlobalExceptionHandler.getThrowableFromIntent(intent)

        val preferences = getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)

        setContent {
            CompositionLocalProvider(LocalTheme provides preferences.currentThemeOption) {
                Theme(LocalTheme.current) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CrashScreen(
                            exception = exception,
                            onRestart = {
                                finishAffinity()
                                startActivity(Intent(this@CrashActivity, MainActivity::class.java))
                            }
                        )
                    }
                }
            }
        }
    }
}