package cc.sovellus.vrcaa.ui.screen.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.helper.isExpiredSession
import cc.sovellus.vrcaa.helper.twoFactorAuth
import cc.sovellus.vrcaa.ui.screen.main.MainScreen
import kotlinx.coroutines.launch

class TwoAuthScreenModel(
    private val context: Context
) : ScreenModel {
    private val api = ApiContext(context)

    var code = mutableStateOf("")

    fun doVerify(otpType: ApiContext.TwoFactorType, token: String, navigator: Navigator) {
        screenModelScope.launch {
            val twoAuth = api.verifyAccount(token, otpType, code.value)
            if (twoAuth.isNotEmpty()) {
                val preferences = context.getSharedPreferences(
                    "vrcaa_prefs", Context.MODE_PRIVATE
                )

                preferences.twoFactorAuth = twoAuth
                preferences.isExpiredSession = false // even if it's false, doesn't matter.

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