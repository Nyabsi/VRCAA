package cc.sovellus.vrcaa.ui.screen.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.api.http.ApiContext
import cc.sovellus.vrcaa.helper.cookies
import cc.sovellus.vrcaa.helper.isExpiredSession
import cc.sovellus.vrcaa.helper.twoFactorAuth
import cc.sovellus.vrcaa.manager.ApiManager
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.service.PipelineService
import cc.sovellus.vrcaa.ui.screen.main.MainScreen
import kotlinx.coroutines.launch

class TwoAuthScreenModel(
    private val context: Context
) : ScreenModel {

    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", Context.MODE_PRIVATE)

    var code: MutableState<String> = mutableStateOf("")

    fun doVerify(otpType: ApiContext.TwoFactorType, token: String, navigator: Navigator) {
        screenModelScope.launch {
            api.verifyAccount(token, otpType, code.value).let {
                if (it.isNullOrEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.login_toast_wrong_code),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    preferences.twoFactorAuth = it
                    preferences.cookies = token
                    preferences.isExpiredSession = false

                    // Set API here, this makes sure that every part of our application can access the API.
                    ApiManager.set(ApiContext(context))

                    val intent = Intent(context, PipelineService::class.java)
                    context.startForegroundService(intent)

                    navigator.popUntilRoot()
                    navigator.replace(MainScreen())
                }
            }
        }
    }
}