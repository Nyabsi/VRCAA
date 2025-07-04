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

import cc.sovellus.vrcaa.base.BaseClient
import cc.sovellus.vrcaa.api.search.SearchAvatar
import com.google.gson.Gson
import net.thauvin.erik.urlencoder.UrlEncoderUtil
import okhttp3.Headers

class JustHPartyProvider : BaseClient() {

    private suspend fun sendRequest(params: String): String?
    {
        val headers = Headers.Builder()
        headers["User-Agent"] = "VRCAA/0.1"
        headers["Referer"] = "vrcaa.sovellus.cc"

        val result = doRequest(
            method = "GET",
            url = "https://avtr.just-h.party/vrcx_search.php$params",
            headers = headers,
            body = null,
            retryAfterFailure = false
        )

        return when (result) {
            is Result.Succeeded -> {
                return result.body
            }
            else -> {
                null
            }
        }
    }

    suspend fun search(query: String): ArrayList<SearchAvatar>
    {
        return when (val result = sendRequest("?search=${UrlEncoderUtil.encode(query)}&n=5000")) {
            is String -> {
                Gson().fromJson(result, Avatars::class.java) ?: arrayListOf()
            }
            else -> arrayListOf()
        }
    }

    suspend fun searchByAuthor(query: String): ArrayList<SearchAvatar>
    {
        return when (val result = sendRequest("?authorId=${UrlEncoderUtil.encode(query)}")) {
            is String -> {
                Gson().fromJson(result, Avatars::class.java) ?: arrayListOf()
            }
            else -> arrayListOf()
        }
    }

    class Avatars : ArrayList<SearchAvatar>()
}