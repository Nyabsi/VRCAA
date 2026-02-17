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

package cc.sovellus.vrcaa.api.search.justhparty

import cc.sovellus.vrcaa.api.search.Config
import cc.sovellus.vrcaa.api.search.models.SearchAvatar
import cc.sovellus.vrcaa.api.search.justhparty.models.JustHPartyResponse
import cc.sovellus.vrcaa.base.BaseClient
import com.google.gson.Gson
import net.thauvin.erik.urlencoder.UrlEncoderUtil
import okhttp3.Headers

class JustHPartyProvider : BaseClient() {

    private suspend fun sendRequest(query: String): String? {
        val result = doRequest(
            method = "GET",
            url =  buildString {
                append(Config.JUST_H_PARTY_API_BASE_URL)
                append("/vrcx_search.php")
                append("?search=${UrlEncoderUtil.encode(query)}")
                append("&n=${5000}")
            },
            headers = GENERIC_HEADER,
            body = null,
            retryAfterFailure = false
        )

        return when (result) {
            is Result.Succeeded -> {
                result.body
            }
            else -> {
                null
            }
        }
    }

    suspend fun search(query: String): ArrayList<SearchAvatar>
    {
        val avatars = when (val result = sendRequest(query)) {
            is String -> {
                Gson().fromJson(result, JustHPartyResponse::class.java) ?: arrayListOf()
            }
            else -> arrayListOf()
        }
        return avatars
    }

    companion object {
        private val GENERIC_HEADER = Headers.Builder()
            .add("User-Agent", Config.API_USER_AGENT)
            .add("Referer", Config.API_REFERER)
            .build()
    }
}