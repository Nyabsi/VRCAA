package cc.sovellus.vrcaa.ui.models.login

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.vrchat.VRChatApi
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import cc.sovellus.vrcaa.ui.screen.navigation.NavigationScreen
import kotlinx.coroutines.launch

class MfaModel(
    private val context: Context,
    private val otpType: VRChatApi.MfaType,
    private val navigator: Navigator
) : ScreenModel {

    var code: MutableState<String> = mutableStateOf("")

    init {
        if (otpType == VRChatApi.MfaType.NONE) {
            val intent = Intent(context, PipelineService::class.java)
            context.startForegroundService(intent)

            navigator.popUntilRoot()
            navigator.replace(NavigationScreen())
        }
    }

    fun verify() {
        screenModelScope.launch {
            if (api?.verifyAccount(otpType, code.value) == true) {
                val intent = Intent(context, PipelineService::class.java)
                context.startForegroundService(intent)

                navigator.popUntilRoot()
                navigator.replace(NavigationScreen())
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.login_toast_wrong_code),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}