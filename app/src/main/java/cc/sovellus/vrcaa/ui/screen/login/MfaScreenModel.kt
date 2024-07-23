package cc.sovellus.vrcaa.ui.screen.login

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cc.sovellus.vrcaa.R
import cc.sovellus.vrcaa.activity.MainActivity
import cc.sovellus.vrcaa.api.vrchat.VRChatApi
import cc.sovellus.vrcaa.extension.twoFactorToken
import cc.sovellus.vrcaa.manager.ApiManager.api
import cc.sovellus.vrcaa.manager.ApiManager.cache
import kotlinx.coroutines.launch

class MfaScreenModel(
    private val context: Context,
    private val otpType: VRChatApi.MfaType,
) : ScreenModel {

    private val preferences: SharedPreferences = context.getSharedPreferences("vrcaa_prefs", MODE_PRIVATE)
    var code: MutableState<String> = mutableStateOf("")

    fun verify() {
        screenModelScope.launch {
            val result = api.verifyAccount(otpType, code.value)
            if (result == null) {
                Toast.makeText(
                    context,
                    context.getString(R.string.login_toast_wrong_code),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                cache.forceCacheRefresh()
                preferences.twoFactorToken = result
                val intent = Intent(context, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }
}