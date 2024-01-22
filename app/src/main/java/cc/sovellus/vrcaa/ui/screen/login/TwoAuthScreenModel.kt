package cc.sovellus.vrcaa.ui.screen.login

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.helper.cookies
import cc.sovellus.vrcaa.helper.twoFactorAuth
import cc.sovellus.vrcaa.ui.screen.main.MainScreen
import kotlinx.coroutines.launch

class TwoAuthScreenModel(
    private val api: ApiContext,
    private val context: Context
) : ScreenModel {

    var code = mutableStateOf("")

    fun doVerify(token: String, navigator: Navigator) {
        screenModelScope.launch {
            val twoAuth = api.verifyAccount(token, ApiContext.TwoFactorType.EMAIL_OTP, code.value)
            if (twoAuth.isNotEmpty()) {
                context.getSharedPreferences(
                    "vrcaa_prefs", Context.MODE_PRIVATE
                ).twoFactorAuth = twoAuth
                navigator.push(MainScreen())
            } else {
                Toast.makeText(
                    context,
                    "Failed to verify, check the code again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}