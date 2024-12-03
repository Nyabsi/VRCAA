package cc.sovellus.vrcaa.ui.screen.login

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.VRChatApi
import cc.sovellus.vrcaa.ui.components.input.CodeInput
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen

class MfaScreen(
    private val otpType: VRChatApi.MfaType
) : Screen {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {


        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val screenModel =
            navigator.rememberNavigatorScreenModel { MfaScreenModel(otpType) }

        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = if (otpType == VRChatApi.MfaType.EMAIL_OTP) {
                        stringResource(R.string.auth_text_email)
                    } else {
                        stringResource(R.string.auth_text_app)
                    }
                )

                CodeInput(
                    input = screenModel.code
                )

                Button(modifier = Modifier
                    .width(200.dp)
                    .padding(4.dp), onClick = {
                    screenModel.verify { result ->
                        if (result) {
                            navigator.replace(NavigationScreen())
                        }
                    }
                }) {
                    Text(text = stringResource(R.string.auth_button_text))
                }

                Button(modifier = Modifier
                    .width(200.dp)
                    .padding(4.dp), onClick = {
                    val clipboard: ClipboardManager? =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                    if (clipboard?.hasPrimaryClip() == true) {
                        val clipData = clipboard.primaryClip
                        if ((clipData?.itemCount ?: 0) > 0) {
                            val clipItem = clipData?.getItemAt(0)
                            val clipText = clipItem?.text?.toString()
                            if (clipText?.length == 6) {
                                screenModel.code.value = clipText
                                screenModel.verify { result ->
                                    if (result) {
                                        navigator.replace(NavigationScreen())
                                    }
                                }
                            }
                        }
                    }
                }) {
                    Text(text = stringResource(R.string.auth_button_paste))
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = padding.calculateBottomPadding()),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.legal_disclaimer),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    fontSize = 12.sp
                )
            }
        }
    }
}