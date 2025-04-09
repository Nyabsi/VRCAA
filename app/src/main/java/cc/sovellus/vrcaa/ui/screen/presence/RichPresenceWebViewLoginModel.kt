package cc.sovellus.vrcaa.ui.screen.presence

import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import cc.sovellus.vrcaa.App
import cc.sovellus.vrcaa.extension.discordToken
import java.io.File

class RichPresenceWebViewLoginModel : ScreenModel {

    private val context: Context = App.getContext()
    private val preferences = context.getSharedPreferences("vrcaa_prefs", 0)

    fun extractToken(): Boolean {

        val webViewStorage = File(context.filesDir.parentFile, "app_webview/Default/Local Storage/leveldb")
            .walkTopDown()
            .filter { it.isFile && it.path.endsWith(".log") }
            .toList()

        var extracted = false
        if (webViewStorage.isNotEmpty())
        {
            File(webViewStorage.first().path).bufferedReader().use { reader ->
                reader.lineSequence().takeWhile { !extracted }.forEach { line ->
                    if (line.contains("token")) {
                        var token = line.substring(line.indexOf("token") + 5)
                        token = token.substring(token.indexOf("\"") + 1 )

                        preferences.discordToken = token
                        extracted = true
                    }
                }
                reader.close()
            }
        }

        return extracted
    }
}