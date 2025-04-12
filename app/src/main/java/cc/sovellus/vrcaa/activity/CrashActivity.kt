package cc.sovellus.vrcaa.activity

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import cc.sovellus.vrcaa.GlobalExceptionHandler
import cc.sovellus.vrcaa.base.BaseActivity
import cc.sovellus.vrcaa.ui.screen.crash.CrashScreen

class CrashActivity : BaseActivity() {

    private lateinit var exception: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        exception = GlobalExceptionHandler.getThrowableFromIntent(intent)
    }

    @Composable
    override fun Content(bundle: Bundle?) {
        CrashScreen(
            exception = exception,
            onRestart = {
                finishAffinity()
                startActivity(Intent(this@CrashActivity, MainActivity::class.java))
            }
        )
    }
}