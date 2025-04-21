/*
 * Copyright (C) 2025. Nyabsi <nyabsi@sovellus.cc>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
                        token = token.substring(0, token.indexOf("\""))

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