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

import cc.sovellus.vrcaa.base.BaseClient
import cc.sovellus.vrcaa.api.search.SearchAvatar
import cc.sovellus.vrcaa.api.search.avtrdb.models.AvtrDbResponse
import com.google.gson.Gson
import okhttp3.Headers

class AvtrDbProvider : BaseClient() {

    suspend fun search(
        query: String,
        n: Int = 50,
        offset: Int = 0
    ): Pair<Boolean, ArrayList<SearchAvatar>>
    {
        val headers = Headers.Builder()

        headers["User-Agent"] = "VRCAA/2.0.0"
        headers["Referer"] = "vrcaa.sovellus.cc"

        val result = doRequest(
            method = "GET",
            url = "https://api.avtrdb.com/v2/avatar/search?query=$query&page_size=$n&page=$offset&legacy=true",
            headers = headers,
            body = null
        )

        return when (result) {
            is Result.Succeeded -> {

                val avatars: ArrayList<SearchAvatar> = arrayListOf()
                val json = Gson().fromJson(result.body, AvtrDbResponse::class.java)

                json.avatars.forEach { avatar ->
                    avatars.add(avatar)
                }

                Pair(!json.hasMore, avatars)
            }
            else -> {
                Pair(false, arrayListOf())
            }
        }
    }
}