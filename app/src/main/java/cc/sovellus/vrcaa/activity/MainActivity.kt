package cc.sovellus.vrcaa.activity


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.SlideTransition
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.base.BaseActivity
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.service.PipelineService
import cc.sovellus.vrcaa.ui.screen.login.LoginScreen
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen

class MainActivity : BaseActivity() {

    private var validSession = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: check is first time launch, redirect to "on-boarding" for permissions.
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

        val invalidSession = intent.extras?.getBoolean("INVALID_SESSION") ?: false
        val terminateSession = intent.extras?.getBoolean("TERMINATE_SESSION") ?: false
        val restartSession = intent.extras?.getBoolean("RESTART_SESSION") ?: false

        if (invalidSession) {

            preferences.authToken = ""

            val intent = Intent(this, PipelineService::class.java)
            stopService(intent)

            Toast.makeText(
                this,
                getString(R.string.api_session_has_expired_text),
                Toast.LENGTH_LONG
            ).show()
        }

        if (restartSession) {
            val intent = Intent(this, PipelineService::class.java)
            stopService(intent)
            startService(intent)
        }

        val token = preferences.authToken
        val twoFactorToken = preferences.twoFactorToken

        validSession = ((token.isNotBlank() && twoFactorToken.isNotEmpty()) && !invalidSession && !terminateSession && !restartSession)

        if (validSession) {
            val intent = Intent(this, PipelineService::class.java)
            startService(intent)
        }
    }

    @Composable
    override fun Content(bundle: Bundle?) {

        Navigator(
            screen = if (validSession) {
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