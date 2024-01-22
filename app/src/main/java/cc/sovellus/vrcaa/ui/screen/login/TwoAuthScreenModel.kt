package cc.sovellus.vrcaa.ui.screen.login

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import cc.sovellus.vrcaa.api.ApiContext
import cc.sovellus.vrcaa.ui.screen.main.MainScreen
import kotlinx.coroutines.launch

class TwoAuthScreenModel(
    private val api: ApiContext,
    private val context: Context
) : ScreenModel {

    var code = mutableStateOf("")

    @SuppressLint("ApplySharedPref")
    fun doVerify(token: String, navigator: Navigator) {
        screenModelScope.launch {
            val cookies = api.verifyAccount(token, ApiContext.TwoFactorType.EMAIL_OTP, code.value)
            if (cookies.isNotEmpty()) {
                // store cookies to preferences
                val editor = context.getSharedPreferences("vrcaa_prefs", 0).edit()
                editor.putString("cookies", cookies)
                editor.commit()

                navigator.popAll()
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