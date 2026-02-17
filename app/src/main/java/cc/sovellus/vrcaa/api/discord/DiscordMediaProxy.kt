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

package cc.sovellus.vrcaa.api.discord

import cc.sovellus.vrcaa.api.discord.models.WebHookResponse
import cc.sovellus.vrcaa.base.BaseClient
import com.google.gson.Gson
import okhttp3.Headers

class DiscordMediaProxy(
    private val webHookUrl: String
)  : BaseClient() {

    private fun handleRequest(result: Result): String? {
        return when (result) {
            is Result.Succeeded -> {
                result.body
            }

            is Result.UnhandledResult -> {
                null
            }

            Result.UnknownMethod -> {
                throw RuntimeException("doRequest was called with unsupported method, supported methods are GET, POST and PUT.")
            }

            else -> { null }
        }
    }

    private suspend fun deleteMessage(id: String) {

        doRequest(
            method = "DELETE",
            url = "$webHookUrl/messages/${id}",
            headers = GENERIC_HEADER,
            body = null
        )
    }

    suspend fun convertImageUrl(url: String?): String? {

        if (url == null)
            return ""

        val body = "{\"content\":null,\"embeds\":[{\"color\":null,\"image\":{\"url\":\"$url\"}}],\"attachments\":[]}"

        val result = doRequest(
            method = "POST",
            url = "$webHookUrl?wait=true",
            headers = GENERIC_HEADER,
            body = body
        )

        val response = handleRequest(result)

        if (result is Result.InvalidRequest)
            return null

        val webhookResponse =  Gson().fromJson(response, WebHookResponse::class.java)
        deleteMessage(webhookResponse.id)
        return "mp:external/${webhookResponse.embeds[0].image.proxyUrl.split('/')[4]}/${url.replace(":/", "")}"
    }

    companion object {
        private val GENERIC_HEADER = Headers.Builder()
            .add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:123.0) Gecko/20100101 Firefox/123.0")
            .build()
    }
}