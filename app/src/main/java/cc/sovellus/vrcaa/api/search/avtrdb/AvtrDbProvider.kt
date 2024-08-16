package cc.sovellus.vrcaa.api.search.avtrdb

import cc.sovellus.vrcaa.api.BaseClient
import cc.sovellus.vrcaa.api.search.SearchAvatar
import cc.sovellus.vrcaa.api.search.avtrdb.models.AvtrDbResponse
import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.Headers

class AvtrDbProvider : BaseClient() {

    suspend fun search(
        query: String,
        n: Int = 50,
        offset: Int = 0,
        avatars: ArrayList<SearchAvatar> = arrayListOf()
    ): ArrayList<SearchAvatar>
    {
        val headers = Headers.Builder()

        headers["User-Agent"] = "VRCAA/2.0.0"
        headers["Referer"] = "vrcaa.sovellus.cc"

        val result = doRequest(
            method = "GET",
            url = "https://api.avtrdb.com/v1/avatar/search?query=$query&page_size=$n&page=$offset",
            headers = headers,
            body = null
        )

        return when (result) {
            is Result.Succeeded -> {

                val temp: ArrayList<SearchAvatar> = avatars
                val json = Gson().fromJson(result.body, AvtrDbResponse::class.java)

                json.avatars.forEach { avatar ->
                    temp.add(
                        SearchAvatar(
                            authorId = avatar.author.vrcId,
                            authorName = avatar.author.name,
                            description = avatar.description,
                            id = avatar.vrcId,
                            imageUrl = avatar.imageUrl,
                            name = avatar.name,
                            releaseStatus = "",
                            thumbnailImageUrl = avatar.thumbnailImageUrl
                        )
                    )
                }

                if (json.hasMore) {
                    // avtrdb.com has rate-limit of 1r/s
                    delay(1000)
                    search(query, n, offset + 1, avatars)
                } else {
                    avatars
                }
            }
            else -> {
                arrayListOf()
            }
        }
    }
}