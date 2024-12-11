package cc.sovellus.vrcaa.ui.components.dialog

import android.content.Intent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.activity.MainActivity
import cc.sovellus.vrcaa.extension.authToken
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.extension.userCredentials
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import kotlinx.coroutines.launch

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.logout_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.logout_dialog_description))
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        var intent = Intent(context, PipelineService::class.java)
                        context.stopService(intent)

                        api.auth.logout()

                        val preferences = context.getSharedPreferences("vrcaa_prefs", 0)
                        preferences.authToken = ""
                        preferences.twoFactorToken = ""
                        preferences.userCredentials = Pair("", "")

                        val bundle = bundleOf()
                        bundle.putBoolean("TERMINATE_SESSION", true)

                        intent = Intent(context, MainActivity::class.java)
                        intent.putExtras(bundle)
                        context.startActivity(intent)
                    }
                }
            ) {
                Text(stringResource(R.string.logout_dialog_continue))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.logout_dialog_cancel))
            }
        }
    )
}