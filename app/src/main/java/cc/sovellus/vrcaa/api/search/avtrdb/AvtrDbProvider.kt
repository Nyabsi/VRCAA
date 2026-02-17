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

package cc.sovellus.vrcaa.api.search.avtrdb

import cc.sovellus.vrcaa.api.search.Config
import cc.sovellus.vrcaa.base.BaseClient
import cc.sovellus.vrcaa.api.search.models.SearchAvatar
import cc.sovellus.vrcaa.api.search.avtrdb.models.AvtrDbResponse
import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.Headers

class AvtrDbProvider : BaseClient() {

    tailrec suspend fun search(
        query: String,
        n: Int = 50,
        offset: Int = 0
    ): Pair<Boolean, ArrayList<SearchAvatar>> {

        val result = doRequest(
            method = "GET",
            url = buildString {
                append(Config.AVTR_DB_API_BASE_URL)
                append("/avatar/search")
                append("?query=${query}")
                append("&page_size=${n}")
                append("&page=${offset}")
                append("&legacy=true")
            },
            headers = GENERIC_HEADER,
            body = null,
            retryAfterFailure = false
        )

        return when (result) {
            is Result.Succeeded -> {

                val avatars: ArrayList<SearchAvatar> = arrayListOf()
                val json = Gson().fromJson(result.body, AvtrDbResponse::class.java)
                avatars.addAll(json.avatars)
                Pair(!json.hasMore, avatars)
            }
            is Result.RateLimited -> {
                delay(1000)
                search(query, n, offset)
            }
            else -> {
                Pair(false, arrayListOf())
            }
        }
    }

    tailrec suspend fun searchAll(
        query: String,
        n: Int = 50,
        offset: Int = 0,
        avatars: ArrayList<SearchAvatar> = arrayListOf()
    ): ArrayList<SearchAvatar> {

        val result = doRequest(
            method = "GET",
            url = buildString {
                append(Config.AVTR_DB_API_BASE_URL)
                append("/avatar/search")
                append("?query=${query}")
                append("&page_size=${n}")
                append("&page=${offset}")
                append("&legacy=true")
            },
            headers = GENERIC_HEADER,
            body = null,
            retryAfterFailure = false
        )

        return when (result) {
            is Result.Succeeded -> {

                val json = Gson().fromJson(result.body, AvtrDbResponse::class.java)
                avatars.addAll(json.avatars)

                if (!json.hasMore)
                    return avatars

                delay(1000) // 1rq/s rl
                searchAll(query, n, offset + 1, avatars)
            }
            is Result.RateLimited -> {
                delay(1000)
                searchAll(query, n, offset, avatars)
            }
            else -> {
                arrayListOf()
            }
        }
    }

    companion object {
        private val GENERIC_HEADER = Headers.Builder()
            .add("User-Agent", Config.API_USER_AGENT)
            .add("Referer", Config.API_REFERER)
            .build()
    }
}